/**                                                                         *
*  This file is part of ftp4che.                                            *
*                                                                           *
*  This library is free software; you can redistribute it and/or modify it  *
*  under the terms of the GNU General Public License as published    		*
*  by the Free Software Foundation; either version 2 of the License, or     *
*  (at your option) any later version.                                      *
*                                                                           *
*  This library is distributed in the hope that it will be useful, but      *
*  WITHOUT ANY WARRANTY; without even the implied warranty of               *
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU        *
*  General Public License for more details.                          		*
*                                                                           *
*  You should have received a copy of the GNU General Public		        *
*  License along with this library; if not, write to the Free Software      *
*  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA  *
*                                                                           *
*****************************************************************************/
package org.ftp4che;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.List;

import javax.net.ServerSocketFactory;



import org.apache.log4j.Logger;
import org.ftp4che.commands.Command;
import org.ftp4che.commands.ListCommand;
import org.ftp4che.commands.RetrieveCommand;
import org.ftp4che.commands.StoreCommand;
import org.ftp4che.exception.AuthenticationNotSupportedException;
import org.ftp4che.exception.ConfigurationException;
import org.ftp4che.exception.FtpIOException;
import org.ftp4che.exception.FtpWorkflowException;
import org.ftp4che.exception.NotConnectedException;
import org.ftp4che.exception.UnkownReplyStateException;
import org.ftp4che.reply.Reply;
import org.ftp4che.util.FTPFile;
import org.ftp4che.util.ReplyFormatter;
import org.ftp4che.util.ReplyWorker;
import org.ftp4che.util.SocketProvider;


/**
 * @author arnold,kurt
 *
 */
public abstract class FTPConnection {
    
    //TODO: support PRET command
    
    
    /* Constants for connection.type*/
    public static final int FTP_CONNECTION = 1;
    public static final int IMPLICIT_SSL_FTP_CONNECTION =  2;
    public static final int AUTH_SSL_FTP_CONNECTION =  3;
    public static final int AUTH_TLS_FTP_CONNECTION =  4;
  
    /* Connection status that are possbile */
    
    public static final int CONNECTED = 1001;
    public static final int DISCONNECTED = 1002;
    public static final int IDLE = 1003;
    public static final int RECEIVING_FILE = 1004;
    public static final int SENDING_FILE = 1005;
    public static final int FXP_FILE = 1006;
    public static final int UNKNOWN = 9999;
    
    
    /* Member variables 
     */
    private static final Logger log = Logger.getLogger(FTPConnection.class.getName());
    private int connectionType = FTPConnection.FTP_CONNECTION; 
    private InetSocketAddress address = null;
    private String user = "";
    private String password = "";
    private String account = "";
    private boolean passiveMode = false;
    private int timeout = 10000;
//  Charset and decoder
    private Charset charset = Charset.forName("ISO-8859-1");
    private CharsetDecoder decoder = charset.newDecoder();
    private CharsetEncoder encoder = charset.newEncoder();
    //TODO: make configurable 
    private ByteBuffer downloadBuffer = ByteBuffer.allocateDirect(65536);
    private ByteBuffer uploadBuffer = ByteBuffer.allocateDirect(8192);
    private CharBuffer controlBuffer = CharBuffer.allocate(4096);
    protected SocketProvider socketProvider = null;
    
    /**
     * @author arnold,kurt
     * @param address Set method for the address the FTPConnection will connect to if connect() is called
     */
     public InetSocketAddress getAddress() {
        return address;
    }
    /**
     * @param address The address to set.
     */
    public void setAddress(InetSocketAddress address)
    {
        this.address = address;
    } 
 
    /**
     * @author arnold,kurt
     * @param password Get method for the password the FTPConnection will use if connect() is called
     */
    public String getPassword() {
        return password;
    }
    /**
     * @author arnold,kurt
     * @param password Set method for the password the FTPConnection will use if connect() is called
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @author arnold,kurt
     * @param user Get method for the user the FTPConnection will use if connect() is called
     * @throws ConfigurationException will be thrown if a parameter is missing or invalid
     */
    public String getUser() {
        return user;
    }
    
    /**
     * @author arnold,kurt
     * @param user Set method for the user the FTPConnection will use if connect() is called
     * @throws ConfigurationException will be thrown if a parameter is missing or invalid
     */
    public void setUser(String user) throws ConfigurationException {
        if(user == null || user.length() == 0)
            throw new ConfigurationException("user must no be null or has a length of 0");
        this.user = user;
    }
 
 
    
    /**
     * @author arnold,kurt
     * @param account Get method for the account the FTPConnection will use if connect() is called
     */
    public String getAccount() {
        return account;
    }
    /**
     * @author arnold,kurt
     * @param account Set method for the account the FTPConnection will use if connect() is called
     */
    public void setAccount(String account) {
        this.account = account;
    }
    
    
    /**
     * This method is used to connect and login to the specified server.
     * @author arnold,kurt
     * @exception NotConnectedException will be thrown if it was not possible to establish a connection to the specified server
     * @exception IOException will be thrown if it there was a problem sending the LoginCommand to the server
     */
    public abstract void connect() throws NotConnectedException,IOException,AuthenticationNotSupportedException,FtpIOException,FtpWorkflowException;
    
    /**
     * This method is used to disconnect from the specified server.
     * @author arnold,kurt
    */
    public void disconnect() {
        try
        {
            Command command = new Command(Command.QUIT);
            sendCommand(command);
        }catch (IOException ioe)
        {
          log.warn("Error closing connection: " + getAddress().getHostName() + ":" + getAddress().getPort(),ioe);
          socketProvider = null;
        }
         
     }
    
    /**
     * This method is used to send commands (there is an implementation for each possible command).
     * You should call this method if you want to send a raw command and get the full results or if there is no implemented corresponding method.
     * @return Reply for the specific command.
     * You will get a result for each server reply. 
     * @author arnold,kurt
     * @exception IOException will be thrown if there was a communication problem with the server
    */
    public Reply sendCommand(Command cmd) throws IOException{
        controlBuffer.clear();
        log.debug("Sending command: " + cmd.toString().substring(0,cmd.toString().length()-2));
        controlBuffer.put(cmd.toString());
        controlBuffer.flip();
        socketProvider.write(encoder.encode(controlBuffer));
        controlBuffer.clear();
        return ReplyWorker.readReply(socketProvider);
     }
    
    /**
     * 
     * This method is used to get the status of your connection
     * @return status there are constants in FTPConnection (f.e. CONNECTED / DISCONNECTED / IDLE ...) where you can identify the status of your ftp connection
     * @author arnold,kurt
     */
    public int getConnectionStatus()
    {
        //TODO: IMPLEMENT / DO WE NEED THIS ???
        return FTPConnection.CONNECTED;
    }
    
    /**
     * This method is used initaly to set the connection timeout. normal you would set it to 10000 (10 sec.). if you have very slow servers try to set it higher.
     * @param millis the milliseconds before a timeout will close the connection
     * @author arnold,kurt
     */
    public void setTimeout(int millis) {
        this.timeout = millis;
    }
    
    /**
     * This method is used initaly to get the connection timeout. normal you would set it to 10000 (10 sec.). if you have very slow servers try to set it higher.
     * @param millis the milliseconds before a timeout will close the connection
     * @author arnold,kurt
     */
    public int getTimeout()
    {
        return timeout;
    }
    
    /**
     * This method is used to change the working directory. it implements the CWD ftp command
     * @param directory a string represanting the new working directory
     * @author arnold,kurt  
     * @throws IOException will be thrown if there was a communication problem with the server
     * @throws FtpWorkflowException will be thrown if there was a ftp reply class 5xx. in most cases wrong commands where send
     * @throws FtpWorkflowException will be thrown if there was a ftp reply class 4xx. this should indicate some secific problems on the server
     */
    public void changeDirectory(String directory) throws IOException,FtpWorkflowException,FtpIOException
    {
        Command command = new Command(Command.CWD,directory);
        Reply reply = sendCommand(command);
        reply.dumpReply();
        reply.validate();
    }
    
    /**
     * This method is used to get the working directory. it implements the PWD ftp command
     * @author arnold,kurt
     * @throws IOException  will be thrown if there was a communication problem with the server
     * @throws UnkownReplyStateException this indicates if the response from the server for the specific commando is not right (f.e. more than one reply line but only one expected)
     * @throws FtpWorkflowException will be thrown if there was a ftp reply class 5xx. in most cases wrong commands where send
     * @throws FtpWorkflowException will be thrown if there was a ftp reply class 4xx. this should indicate some secific problems on the server
     */
    public String getWorkDirectory() throws IOException,UnkownReplyStateException,FtpWorkflowException,FtpIOException
    {
        Command command = new Command(Command.PWD);
        Reply reply = sendCommand(command);
        reply.dumpReply();
        reply.validate();
        return ReplyFormatter.parsePWDReply(reply);
    }
    
    /**
     * This method is used to change to the parent directory. it implements the CDUP ftp command
     * @author arnold,kurt
     * @throws IOException  will be thrown if there was a communication problem with the server
     * @throws FtpWorkflowException will be thrown if there was a ftp reply class 5xx. in most cases wrong commands where send
     * @throws FtpWorkflowException will be thrown if there was a ftp reply class 4xx. this should indicate some secific problems on the server
     */
    public void changeToParentDirectory() throws IOException,FtpWorkflowException,FtpIOException
    {
        Command command = new Command(Command.CDUP);
        Reply reply = sendCommand(command);
        reply.dumpReply();
        reply.validate();
    }
    
    /**
     * This method is used to create a new directory. it implements the MKD ftp command
     * @param pathname a string represanting the directory to create
     * @author arnold,kurt
     * @throws IOException will be thrown if there was a communication problem with the server
     * @throws FtpWorkflowException will be thrown if there was a ftp reply class 5xx. in most cases wrong commands where send
     * @throws FtpWorkflowException will be thrown if there was a ftp reply class 4xx. this should indicate some secific problems on the server
     */
    public void makeDirectory(String pathname) throws IOException,FtpWorkflowException,FtpIOException
    {
        Command command = new Command(Command.MKD,pathname);
        Reply reply = sendCommand(command);
        reply.dumpReply();
        reply.validate();
    }

    /**
     * This method is used to remove a specific directory. it implements the RMD ftp command
     * @param pathname a string represanting the directory to remove
     * @author arnold,kurt
     * @throws IOException will be thrown if there was a communication problem with the server
     * @throws FtpWorkflowException will be thrown if there was a ftp reply class 5xx. in most cases wrong commands where send
     * @throws FtpWorkflowException will be thrown if there was a ftp reply class 4xx. this should indicate some secific problems on the server
     */
    public void removeDirectory( String pathname ) throws IOException,FtpWorkflowException,FtpIOException
    {
        Command command = new Command(Command.RMD,pathname);
        Reply reply = sendCommand(command);
        reply.dumpReply();
        reply.validate();
    }
    
    /**
     * This method is used to send a noop comand to the server i.g. for keep alive purpose. 
     * it implements the NOOP ftp command
     * @author arnold,kurt
     * @throws IOException will be thrown if there was a communication problem with the server
     * @throws FtpWorkflowException will be thrown if there was a ftp reply class 5xx. in most cases wrong commands where send
     * @throws FtpWorkflowException will be thrown if there was a ftp reply class 4xx. this should indicate some secific problems on the server
     */
    public void noOperation() throws IOException,FtpWorkflowException,FtpIOException
    {
        Command command = new Command(Command.NOOP);
        Reply reply = sendCommand(command);
        reply.dumpReply();
        reply.validate();
    }
    
    /**
     * This method is used to go into passive mode. it implements the PASV ftp command
     * @author arnold,kurt
     * @throws IOException will be thrown if there was a communication problem with the server
     * @throws FtpWorkflowException will be thrown if there was a ftp reply class 5xx. in most cases wrong commands where send
     * @throws FtpWorkflowException will be thrown if there was a ftp reply class 4xx. this should indicate some secific problems on the server
     */
    public InetSocketAddress sendPassiveMode() throws IOException,FtpWorkflowException,FtpIOException
    {
    	Command command = new Command(Command.PASV);
    	try
    	{
            Reply reply = sendCommand(command);
            reply.dumpReply();
            reply.validate();
    		return ReplyFormatter.parsePASVCommand(reply);
    	}catch (UnkownReplyStateException urse)
    	{
    		log.error("The state of the reply from pasv command is unknown!",urse);
    	}
    	return null;
    }
    
    /**
     * This method is used initaly to set if passive mode should be used. 
     * Default it is false
     * @param passive if true it will use passive mode
     * @author arnold,kurt
     */
    public void setPassiveMode(boolean mode) {
        this.passiveMode = mode;  
      }
      
    /**
     * This method is used initaly to set if passive mode should be used. 
     * Default it is false
     * @param passive if true it will use passive mode
     * @author arnold,kurt
     */
    public boolean isPassiveMode() {
          return passiveMode;
    }
    
    /**
     * This method is used to get a directory listing from the current working directory
     * @return List of FTPFiles
     * @throws IOException will be thrown if there was a communication problem with the server
     * @throws FtpWorkflowException will be thrown if there was a ftp reply class 5xx. in most cases wrong commands where send
     * @throws FtpWorkflowException will be thrown if there was a ftp reply class 4xx. this should indicate some secific problems on the server
     */
    
    public List<FTPFile> getDirectoryListing() throws IOException,FtpWorkflowException,FtpIOException
    {
       return getDirectoryListing(".");
    }
    
    public List<String> getFastDirectoryListing() throws IOException,FtpWorkflowException,FtpIOException
    {
        Command command = new Command(Command.STAT,"-LA");
        return (sendCommand(command)).getLines();
    }

    /**
     * This method is used to get a directory listing from the specified directory
     * @return List of FTPFiles
     * @param The directory where a LIST should be done
     * @throws IOException will be thrown if there was a communication problem with the server
     * @throws FtpWorkflowException will be thrown if there was a ftp reply class 5xx. in most cases wrong commands where send
     * @throws FtpWorkflowException will be thrown if there was a ftp reply class 4xx. this should indicate some secific problems on the server
     */
    
    public List<FTPFile> getDirectoryListing(String directory) throws IOException,FtpWorkflowException,FtpIOException
    {
    	ListCommand command = new ListCommand(directory);
    	SocketProvider provider = null;
        
        if ( getConnectionType() != FTPConnection.FTP_CONNECTION &&
             getConnectionType() != FTPConnection.IMPLICIT_SSL_FTP_CONNECTION ) {
            Command pbsz = new Command(Command.PBSZ,"0");
            (sendCommand(pbsz)).dumpReply();
            Command prot = new Command(Command.PROT,"P");
            (sendCommand(prot)).dumpReply();
        }
        Reply commandReply = new Reply();
        if(isPassiveMode())
        {
        	provider = initDataSocket(command,commandReply);
        }
        else
        {
        	provider = sendPortCommand(command,commandReply);
        }
      
        command.setDataSocket(provider);
        //INFO response from ControllConnection is ignored
        List<FTPFile> parsedList = ReplyFormatter.parseListReply(command.fetchDataConnectionReply());
        if(commandReply.getLines().size() == 1)
        {
            (ReplyWorker.readReply(socketProvider)).dumpReply();
        }
        return parsedList;
    }
    
    /**
     * This method is used to tell the server that we want to go in PORT mode (means that we tell the server he should open a connection to a port)
     * @return SocketProvider with the established connection
     * @param The command that will follow after establishing the connection
     * @throws IOException will be thrown if there was a communication problem with the server
     * @throws FtpWorkflowException will be thrown if there was a ftp reply class 5xx. in most cases wrong commands where send
     * @throws FtpWorkflowException will be thrown if there was a ftp reply class 4xx. this should indicate some secific problems on the server
     */
    public SocketProvider sendPortCommand(Command command,Reply commandReply) throws IOException,FtpWorkflowException,FtpIOException
    {
    	ServerSocket server = ServerSocketFactory.getDefault().createServerSocket();
    	InetSocketAddress isa = new InetSocketAddress(socketProvider.socket().getLocalAddress(), 0);
    	server.bind(isa);
    	int port = server.getLocalPort();

    	StringBuffer modifiedHost = new StringBuffer();
    	modifiedHost.append(server.getInetAddress().getHostAddress().replace('.',','));
    	modifiedHost.append(",");
    	modifiedHost.append(port >> 8);
    	modifiedHost.append(",");
    	modifiedHost.append(port & 0x00ff);
        
    	Command portCommand = new Command(Command.PORT,modifiedHost.toString());
        Reply portReply = sendCommand(portCommand);
        portReply.dumpReply();
        portReply.validate();
        commandReply.setLines(sendCommand(command).getLines());
        commandReply.dumpReply();
        commandReply.validate();
        SocketProvider provider = new SocketProvider(server.accept(),false);
        provider.socket().setReceiveBufferSize(65536);
        provider.socket().setSendBufferSize(65536);
        provider.setSSLMode(getConnectionType());
        if(connectionType == FTPConnection.AUTH_TLS_FTP_CONNECTION || connectionType == FTPConnection.AUTH_SSL_FTP_CONNECTION)
            provider.negotiate();
        
        return provider;

    }
    
    
    /**
     * This method is used to download a file from the server to a specifed File object
     * @param fromFile the file on the server
     * @param toFile the file object where the file should be stored (on the local computer)
     * @throws IOException will be thrown if there was a communication problem with the server
     * @throws FtpWorkflowException will be thrown if there was a ftp reply class 5xx. in most cases wrong commands where send
     * @throws FtpWorkflowException will be thrown if there was a ftp reply class 4xx. this should indicate some secific problems on the server
     * @throws FtpFileNotFountException will be thrown if the specified fromFile is not found on the server
     */
    
    public void downloadFile(FTPFile fromFile,FTPFile toFile) throws IOException,FtpWorkflowException,FtpIOException
    {
    	
    	RetrieveCommand command = new RetrieveCommand(Command.RETR,fromFile,toFile);
    	SocketProvider provider = null;
        
        if ( getConnectionType() != FTPConnection.FTP_CONNECTION &&
             getConnectionType() != FTPConnection.IMPLICIT_SSL_FTP_CONNECTION ) {
            Command pbsz = new Command(Command.PBSZ,"0");
            (sendCommand(pbsz)).dumpReply();
            Command prot = new Command(Command.PROT,"P");
            (sendCommand(prot)).dumpReply();
        }   
        
        Reply commandReply = new Reply();
        if(isPassiveMode())
        {
        	provider = initDataSocket(command,commandReply);
        }
        else
        {
        	provider = sendPortCommand(command,commandReply);
        }
   
        command.setDataSocket(provider);
        //INFO response from ControllConnection is ignored
        command.fetchDataConnectionReply();
        if(commandReply.getLines().size() == 1)
        {
            (ReplyWorker.readReply(socketProvider)).dumpReply();
        }
    }
    
    /**
     * This method is used to upload a file to the server to a specifed FtpFile
     * @param fromFile the file on the local computer
     * @param toFile the FtpFile object where the file should be stored (on the server)
     * @throws IOException will be thrown if there was a communication problem with the server
     * @throws FtpWorkflowException will be thrown if there was a ftp reply class 5xx. in most cases wrong commands where send
     * @throws FtpWorkflowException will be thrown if there was a ftp reply class 4xx. this should indicate some secific problems on the server
     * @throws FileNotFountException will be thrown if the specified fromFile is not found on the local computer
     */
    public void uploadFile(FTPFile fromFile,FTPFile toFile) throws IOException,FtpWorkflowException,FtpIOException
    {
    	
    	StoreCommand command = new StoreCommand(Command.STOR,fromFile,toFile);
    	SocketProvider provider = null;
        
        if ( getConnectionType() != FTPConnection.FTP_CONNECTION &&
             getConnectionType() != FTPConnection.IMPLICIT_SSL_FTP_CONNECTION ) {
            Command pbsz = new Command(Command.PBSZ,"0");
            (sendCommand(pbsz)).dumpReply();
            Command prot = new Command(Command.PROT,"P");
            (sendCommand(prot)).dumpReply();
        }
        
        Reply commandReply = new Reply();
        if(isPassiveMode())
        {
        	provider = initDataSocket(command,commandReply);
        }
        else
        {
        	provider = sendPortCommand(command,commandReply);
        }
      
        command.setDataSocket(provider);
        //INFO response from ControllConnection is ignored
      	command.fetchDataConnectionReply();
        if(commandReply.getLines().size() == 1)
        {
            (ReplyWorker.readReply(socketProvider)).dumpReply();
        }
    }
    
    public void fxpFile(FTPConnection destination, FTPFile fromFile,FTPFile toFile) throws IOException,FtpWorkflowException,FtpIOException
    {
        // send PASV to source site
        Command pasvCommand = new Command(Command.PASV);
        Reply pasvReply = sendCommand(pasvCommand);
        pasvReply.dumpReply();
        pasvReply.validate(); 
        
        // parse the host and port from reply
        List<String> lines = pasvReply.getLines();
        if(lines.size() != 1)
            throw new UnkownReplyStateException("PASV Reply has to have a size of 1 entry but it has: " + lines.size());
        String line = lines.get(0);
        line = line.substring(line.indexOf('(')+1,line.lastIndexOf(')'));
        
        
        // send PORT to destination site
        Command portCommand = new Command(Command.PORT, line);
        Reply portReply = destination.sendCommand(portCommand);
        portReply.dumpReply();
        portReply.validate();
        
        // send STOR command to destination site
        Command storeCommand = new Command(Command.STOR, toFile.getName());
        Reply storeReply = destination.sendCommand(storeCommand);
        storeReply.dumpReply();
        storeReply.validate();
        
        // send RETR command to source site
        Command retrCommand = new Command(Command.RETR,fromFile.getName());
        Reply retrReply = sendCommand(retrCommand);
        retrReply.dumpReply();
        retrReply.validate();   
    }
    
    private SocketProvider initDataSocket(Command command,Reply commandReply) throws IOException,FtpIOException,FtpWorkflowException
    {
        InetSocketAddress dataSocket = sendPassiveMode();
        SocketProvider provider = new SocketProvider(false);
        provider.connect(dataSocket);
        provider.setSSLMode(getConnectionType());
        
        commandReply.setLines(sendCommand(command).getLines());
        commandReply.dumpReply();
        commandReply.validate();

        
        if(connectionType == FTPConnection.AUTH_TLS_FTP_CONNECTION || connectionType == FTPConnection.AUTH_SSL_FTP_CONNECTION)
            provider.negotiate();
        
        return provider;
    }


    /**
     * @return Returns the connectionType.
     */
    public int getConnectionType() {
        return connectionType;
    }


    /**
     * @param connectionType The connectionType to set.
     */
    public void setConnectionType(int connectionType) {
        this.connectionType = connectionType;
    }
}
