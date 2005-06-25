/*
 * Created on 11.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ftp4che.impl;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.ftp4che.FTPConnection;
import org.ftp4che.exception.NotConnectedException;
import org.ftp4che.util.ReplyListener;



/**
 * @author arnold
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NormalFTPConnection extends FTPConnection {
    
    Logger log = Logger.getLogger(NormalFTPConnection.class.getName());
    ReplyListener listener = null;
    
    public NormalFTPConnection()
    {
        super();
    }
    
    @Override
    public void connect() throws NotConnectedException, IOException {
        // TODO Auto-generated method stub
        
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //TODO: REMOVE OLD SHIT CODE
    
//
//    /* (non-Javadoc)
//     * @see org.ftp4che.FTPConnection#connect()
//     */
//    public void connect() throws NotConnectedException{
//
//        try
//        {
//            socketProvider = new SocketProvider();
//        }catch (IOException ioe)
//        {
//            String error = "Error creating SocketProvider: " + ioe.getMessage();
//            log.error(error,ioe);
//            throw new NotConnectedException(error);
//        }
//        // Only for logging
//        String hostAndPort = getAddress().getHostName() + ":" + getAddress().getPort();
//        try
//        {
//            if(socketProvider.connect(getAddress()))
//            {	
//                log.debug("connected to:" + hostAndPort);
//                listener = new ReplyListener(socketProvider);
//            }
//            else
//            {
//                String error = "Couln't not connect to: " + hostAndPort;
//                log.error(error);
//                throw new NotConnectedException(error);
//            }
//        }catch (IOException ioe)
//        {
//            String error = "Error connection to:" + hostAndPort;
//            log.error(error,ioe);
//            throw new NotConnectedException(error);
//        }
//    }
//
//    /* (non-Javadoc)
//     * @see org.ftp4che.FTPConnection#login()
//     */
//    public void login() throws IOException{
//        login = new LoginCommand(getUser(),getPassword(),getAccount());
//        login.setResult(sendCommand(login));
//        Result[] results = login.getResult();
//        dumpResult(results);
//    }
//
//    /* (non-Javadoc)
//     * @see org.ftp4che.FTPConnection#connectAndLogin()
//     */
//    public void connectAndLogin() throws NotConnectedException,IOException {
//        connect();
//        login();
//    }
//
//    /* (non-Javadoc)
//     * @see org.ftp4che.FTPConnection#disconnect()
//     */
//  
//
//    /* (non-Javadoc)
//     * @see org.ftp4che.FTPConnection#sendCommand(org.ftp4che.Command)
//     */
//    
//    public Reply sendCommand(Command cmd) throws IOException{
//         controlBuffer.clear();
//         log.debug("Sending command: " + cmd.buildCommand());
//         controlBuffer.put(cmd.toString());
//         controlBuffer.flip();
//    	 socketProvider.write(encoder.encode(controlBuffer));
//    	 controlBuffer.clear();
//    	 return cmd.fetchReply();
////    		log.debug("Initialising reply listener...");
////    		log.debug("the result value is: " + listener.getResultValue());
////    		while (listener.getResultValue() == 0) {
////    		        try {
////    		            Thread.sleep(20);
////    			} catch (InterruptedException e) {}
////    		    }
////    		    results[i] = ResultFactory.getResultForCommand(cmd);
////    		    results[i].setResultLines(new ArrayList(listener.getLines()));
////    		    results[i].setResultValue(listener.getResultValue());
////    		    listener.getLines().clear();
////    		    listener.setLine("");
////    		    listener.setResultValue(0);
////    		    listener.setStartTime(0);
////    		}
////    		else
////    		    return null;
////        }
//    }
//
//    /* (non-Javadoc)
//     * @see org.ftp4che.FTPConnection#getConnectionStatus()
//     */
//    public int getConnectionStatus() {
//        // TODO Auto-generated method stub
//        return 0;
//    }
//
//    /* (non-Javadoc)
//     * @see org.ftp4che.FTPConnection#setTimeout(long)
//     */
//  
//
//    /* (non-Javadoc)
//     * @see org.ftp4che.FTPConnection#changeDirectory(java.lang.String)
//     */
//    public void changeDirectory(String directory) throws IOException{
//        CWDCommand command = new CWDCommand(directory);
//        Result[] results = sendCommand(command);
//        dumpResult(results);
//    }
//
//    /* (non-Javadoc)
//     * @see org.ftp4che.FTPConnection#getWorkDirectory()
//     */
//    public String getWorkDirectory() throws IOException{
//        PWDCommand command = new PWDCommand();
//        Result[] results = sendCommand(command);
//        dumpResult(results);
//        
//        //TODO: parse directory from results ! do this in PWDCommand
//        return "";
//    }
//    
//    
//    public void run()
//    {
//        //TODO: do keep alive stuff here 
//        
//    }
//
//    /* (non-Javadoc)
//     * @see org.ftp4che.FTPConnection#getParentDirectory()
//     */
//    public String changeToParentDirectory() throws IOException {
//        CDUPCommand command = new CDUPCommand();
//        Result[] results = sendCommand(command);
//        dumpResult(results);
//        return " ";
//    }
//    
//    /* (non-Javadoc)
//     * @see org.ftp4che.FTPConnection#makeDirectory()
//     */
//    public boolean makeDirectory( String pathname ) throws IOException {
//        MKDCommand command = new MKDCommand( pathname );
//        Result[] results = sendCommand( command );
//        dumpResult( results );
//        /* @todo return true if directory got created, else false */
//        return false;
//    }
//
//    /* (non-Javadoc)
//     * @see org.ftp4che.FTPConnection#removeDirectory()
//     */
//    public boolean removeDirectory( String pathname ) throws IOException {
//        RMDCommand command = new RMDCommand( pathname );
//        Result[] results = sendCommand( command );
//        dumpResult( results );
//        /* @todo return true if directory got deleted, else false */
//        return false;
//    }
//
//    /* (non-Javadoc)
//     * @see org.ftp4che.FTPConnection#noOperation()
//     */
//    public void noOperation() throws IOException {
//        NOOPCommand command = new NOOPCommand();
//        Result[] results = sendCommand( command );
//        dumpResult( results );
//    }
//
//    /* (non-Javadoc)
//     * @see org.ftp4che.FTPConnection#setPassiveMode()
//     */
//    public boolean sendPassiveMode() throws IOException {
//        PASVCommand command = new PASVCommand();
//        Result[] results = sendCommand( command );
//        dumpResult( results );
//        return false;
//    }
//    
//    public void dumpResult(Result[] result)
//    {
//        for(int i = 0; i < result.length; i++)
//        {
//            log.info(result[i]);
//        }
//    }
//    
//    public List getDirectoryListing() throws IOException
//    {
//       return getDirectoryListing(getWorkDirectory());
//    }
//    
//    public List getDirectoryListing(String directory) throws IOException
//    {
//        directory = "/home/ftpuser/download";
//        LISTCommand command = new LISTCommand(directory);
//        SocketProvider dataConnection = null;
//        if(isPassiveMode())
//        {
//            dataConnection = sendPassiveMode(command);
//        }
//        else
//        {
//            dataConnection = acceptConnection(command);
//        }
//    	StringBuffer output = new StringBuffer(32000);
//		int amount;
//		downloadBuffer.clear();
//		long start = System.currentTimeMillis();
//		CharBuffer cb;
//		while ((amount = dataConnection.read(downloadBuffer)) != -1) {
//			if (amount == 0) {
//				if ((System.currentTimeMillis() - start) > getTimeout()) {
//					break;
//				}
//				try {
//					Thread.sleep(5);
//				} catch (InterruptedException e) {}
//			} else {
//				start = System.currentTimeMillis();
//			}
//
//			downloadBuffer.flip();
//			cb = decoder.decode(downloadBuffer);
//			output.append(cb);
//			downloadBuffer.clear();
//		}
//		dataConnection.close();
//		listener.setResultValue(0);
//        
//        LISTResult result = new LISTResult();
//        List lines = new ArrayList();
//        String[] splittedLines = output.toString().split("\n");
//		for(int i=0; i < splittedLines.length;i++)
//		    lines.add(splittedLines[i]);
//		result.setResultLines(lines);
//        return result.getFileList();
//    }
//    
//	/**
//	 *  gets a SocketChannel for a datatransfer after some command is send
//	 *
//	 *@param  cmd              the command that needs a dataport
//	 *@return                  Description of the Returned Value
//	 *@exception  IOException  Description of Exception
//	 */
//	public SocketProvider acceptConnection(Command cmd) throws IOException {
//		ServerSocketChannel ssc = ServerSocketChannel.open();
//		InetSocketAddress isa = new InetSocketAddress(socketProvider.socket().getLocalAddress(), 0);
//		SocketProvider sc = null;
//		ssc.socket().bind(isa); 
//		sendPortCommand(ssc.socket().getInetAddress(),ssc.socket().getLocalPort());
//		sendCommand(cmd);
//		sc = new SocketProvider(ssc.accept());
//		while (!sc.finishConnect()) {
//			try {
//				Thread.sleep(20);
//			} catch (InterruptedException e) {}
//		}
//		sc.socket().setReceiveBufferSize(65536);
//		sc.socket().setSendBufferSize(65536);
//
//		return sc;
//	}
//	
//	public void sendPortCommand(InetAddress inetaddress, int localport) throws IOException 
//	{
//		byte[] addrbytes = inetaddress.getAddress();
//		short addrshorts[] = new short[4];
//		for (int i = 0; i <= 3; i++) {
//			addrshorts[i] = addrbytes[i];
//			if (addrshorts[i] < 0) {
//				addrshorts[i] += 256;
//			}
//		}
//		PORTCommand command = new PORTCommand(addrshorts[0] + "," +
//				addrshorts[1] + "," + addrshorts[2] + "," +
//				addrshorts[3] + "," + ((localport & 0xff00) >>
//				8) + "," + (localport & 0x00ff));
//		sendCommand(command);
//	}
//	
//	public SocketProvider sendPassiveMode(Command command) throws IOException {
//		InetSocketAddress isa = null;
//		SocketProvider sc = null;
//		downloadBuffer.clear();
//		try {
//		    PASVCommand passiveCommand = new PASVCommand();
//		    Result result[] = sendCommand(passiveCommand);
//		    int i,j = 0;
//			for(i = 0; i < result.length; i++)
//			{
//			    boolean found = false;
//			    for(j = 0; j < result[i].getResultLines().size(); j++)
//			    {
//			        if(((String)result[i].getResultLines().get(j)).indexOf(")") != -1)
//			        {
//			            found = true;
//			            break;
//			        }
//			    }
//			    if(found)
//			        break;
//			}
//			String line = ((String)result[i].getResultLines().get(j));
//			if(line.indexOf(",") == -1)
//			{
//			    return null;
//			}
//			line = line.substring(line.indexOf("(") + 1,
//					line.indexOf(")"));
//			String[] parts = line.split(",");
//			String ip = parts[0] + "." + parts[1] + "." +
//					parts[2] + "." + parts[3];
//			int port = Integer.parseInt(parts[4]) * 256 +
//					Integer.parseInt(parts[5]);
//			sendCommand(command,false);
//			isa = new InetSocketAddress(InetAddress.getByName(ip), port);
//			sc = new SocketProvider();
//			sc.socket().setReceiveBufferSize(65536);
//			sc.socket().setSendBufferSize(65536);
//			sc.configureBlocking(false);
//			sc.connect(isa);
//			
//			while (!sc.finishConnect()) {
//				try {
//					Thread.sleep(20);
//				} catch (InterruptedException e) {}
//			}
//			clearResult();
//			return sc;
//		} catch (CharacterCodingException e) {
//		    log.error(e,e);
//		}
//		return sc;
//	}
//	
//	
//	public void clearResult() throws IOException {
//		listener.setStartTime(System.currentTimeMillis());
//		while (listener.getResultValue() == 0) {
//			try {
//				sleep(50);
//			} catch (InterruptedException e) {}
//		}
//		listener.setResultValue(0);
//		listener.setStartTime(0);
//		for(int i = 0; i < listener.getLines().size(); i++)
//		{
//		    log.debug("Skipping line: " + listener.getLines().get(i));
//		}
//		listener.getLines().clear();
//	}

 
 }
