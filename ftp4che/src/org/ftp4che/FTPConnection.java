/*
 * Created on 11.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ftp4che;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;



import org.ftp4che.exception.ConfigurationException;
import org.ftp4che.exception.NotConnectedException;


/**
 * @author arnold,kurt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface FTPConnection {
    
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
    
    /**
     * @author arnold,kurt
     * @param address Set method for the address the FTPConnection will connect to if connect() is called
     */
    public void setAddress(InetSocketAddress address);
    
    /**
     * @author arnold,kurt
     * @param user Set method for the user the FTPConnection will use if connect() is called
     * @throws ConfigurationException will be thrown if a parameter is missing or invalid
     */
    public void setUser(String user) throws ConfigurationException;
    
    /**
     * @author arnold,kurt
     * @param password Set method for the password the FTPConnection will use if connect() is called
     */
    public void setPassword(String password);
    
    /**
     * @author arnold,kurt
     * @param account Set method for the account the FTPConnection will use if connect() is called
     */
    public void setAccount(String account);
    
    /**
     * This method is used to connect to the specified server. it only connects to it. it doesn't login. if you want to login to 
     * the server call login() after connect() or call connectAndLogin() instead of connect()
     * @author arnold,kurt
     * @exception NotConnectedException will be thrown if it was not possible to establish a connection to the specified server
     */
    public void connect() throws NotConnectedException;
    
    /**
     * This method is used to login to the specified server.
     * @author arnold,kurt
     * @exception IOException will be thrown if it there was a problem sending the LoginCommand to the server
     */
    public void login() throws IOException;
    
    /**
     * This method is used to connect and login to the specified server.
     * @author arnold,kurt
     * @exception NotConnectedException will be thrown if it was not possible to establish a connection to the specified server
     * @exception IOException will be thrown if it there was a problem sending the LoginCommand to the server
     */
    public void connectAndLogin() throws NotConnectedException,IOException;
    
    /**
     * This method is used to disconnect from the specified server.
     * @author arnold,kurt
    */
    public void disconnect();
    
    /**
     * This method is used to send commands (there is an implementation for each possible command).
     * You should call this method if you want to send a raw command and get the full results or if there is no implemented corresponding method.
     * @return Result[] a field of results for the specific command. f.e. sending a CDUPCommand results in an array of CDUPResults.
     * Some commands need more than one line to be send to the server. f.e. LoginCommand has USER -> PASS -> optionaly ACCT.
     * You will get a result for each server reply. 
     * @author arnold,kurt
     * @exception IOException will be thrown if there was a communication problem with the server
    */
    public Result[] sendCommand(Command cmd) throws IOException;
    
    /**
     * 
     * This method is used to get the status of your connection
     * @return status there are constants in FTPConnection (f.e. CONNECTED / DISCONNECTED / IDLE ...) where you can identify the status of your ftp connection
     * @author arnold,kurt
     */
    public int getConnectionStatus();
    
    /**
     * This method is used initaly to set the connection timeout. normal you would set it to 10000 (10 sec.). if you have very slow servers try to set it higher.
     * @param millis the milliseconds before a timeout will close the connection
     * @author arnold,kurt
     */
    public void setTimeout(long millis);
    
    /**
     * This method is used to change the working directory. it implements the CWD ftp command
     * @param directory a string represanting the new working directory
     * @author arnold,kurt  
     * @throws IOException will be thrown if there was a communication problem with the server
     */
    public void changeDirectory(String directory) throws IOException;
    
    /**
     * This method is used to get the working directory. it implements the PWD ftp command
     * @author arnold,kurt
     * @throws IOException  will be thrown if there was a communication problem with the server
     */
    public String getWorkDirectory() throws IOException;
    
    /**
     * This method is used to change to the parent directory. it implements the CDUP ftp command
     * @author arnold,kurt
     * @throws IOException  will be thrown if there was a communication problem with the server
     */
    public String changeToParentDirectory() throws IOException;
    
    /**
     * This method is used to create a new directory. it implements the MKD ftp command
     * @param pathname a string represanting the directory to create
     * @author arnold,kurt
     * @throws IOException will be thrown if there was a communication problem with the server
     */
    public boolean makeDirectory( String pathname ) throws IOException;

    /**
     * This method is used to remove a specific directory. it implements the RMD ftp command
     * @param pathname a string represanting the directory to remove
     * @author arnold,kurt
     * @throws IOException will be thrown if there was a communication problem with the server
     */
    public boolean removeDirectory( String pathname ) throws IOException;
    
    /**
     * This method is used to send a noop comand to the server i.g. for keep alive purpose. 
     * it implements the NOOP ftp command
     * @author arnold,kurt
     * @throws IOException will be thrown if there was a communication problem with the server
     */
    public void noOperation() throws IOException;
    
    /**
     * This method is used to go into passive mode. it implements the PASV ftp command
     * @author arnold,kurt
     * @throws IOException will be thrown if there was a communication problem with the server
     */
    public boolean sendPassiveMode() throws IOException;
    
    /**
     * This method is used initaly to set if passive mode should be used. 
     * Default it is false
     * @param passive if true it will use passive mode
     * @author arnold,kurt
     */
    public void setPassiveMode(boolean mode);
    
    public List getDirectoryListing() throws IOException;
    public List getDirectoryListing(String directory) throws IOException;
    public void sendPortCommand(InetAddress inetaddress, int localport) throws IOException;
}
