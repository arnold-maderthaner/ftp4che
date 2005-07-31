package org.ftp4che.impl;

import java.io.IOException;
import java.util.List;


import org.apache.log4j.Logger;
import org.ftp4che.FTPConnection;
import org.ftp4che.commands.Command;
import org.ftp4che.exception.AuthenticationNotSupportedException;
import org.ftp4che.exception.NotConnectedException;
import org.ftp4che.reply.Reply;
import org.ftp4che.reply.ReplyCode;
import org.ftp4che.util.ReplyWorker;
import org.ftp4che.util.SocketProvider;

public class AuthTLSFTPConnection extends FTPConnection {
	public static final String AUTH_STRING = "AUTH TLS";
	Logger log = Logger.getLogger(AuthTLSFTPConnection.class.getName());
	    
    @Override
    public void connect() throws NotConnectedException, IOException,AuthenticationNotSupportedException {
    	  try
          {
              socketProvider = new SocketProvider();
              
              socketProvider.setSSLMode(FTPConnection.FTP_CONNECTION);
          }catch (IOException ioe)
          {
              String error = "Error creating SocketProvider: " + ioe.getMessage();
              log.error(error,ioe);
              throw new NotConnectedException(error);
          }
          // Only for logging
          String hostAndPort = getAddress().getHostName() + ":" + getAddress().getPort();
          try
          {
              if(socketProvider.connect(getAddress()))
              {   
                  log.debug("connected to:" + hostAndPort);
                  socketProvider.socket().setSoTimeout(getTimeout());
                  socketProvider.socket().setKeepAlive(true);
                  socketProvider.configureBlocking(false);
              }
              else
              {
                  String error = "Couln't not connect to: " + hostAndPort;
                  log.error(error);
                  throw new NotConnectedException(error);
              }
          }catch (IOException ioe)
          {
              String error = "Error connection to:" + hostAndPort;
              log.error(error,ioe);
              throw new NotConnectedException(error);
          }
          //Till here the connection is not encrypted!!
          (ReplyWorker.readReply(socketProvider)).dumpReply(System.out);
          Reply reply = sendCommand(new Command(Command.FEAT));
          String authCommand = AUTH_STRING;
          if(ReplyCode.isPositiveCompletionReply(reply))
          {
        	  List<String> lines = reply.getLines();
        	  boolean found = false;
             
        	  for(String s : lines)
        	  {
        		  if(s.indexOf(AUTH_STRING) > -1)
        		  {
                    authCommand = s;
        			found = true;
        			break;
        		  }
        	  } 
        	  if(found)
        	  {
                  negotiateAndLogin(authCommand);
        	  }
        	  else
       		  {
       			  throw new AuthenticationNotSupportedException(authCommand + " not supported by server (not listed in FEAT command)");
       		  }
          }
          else
          {
              negotiateAndLogin(authCommand);
          }
          socketProvider.setSSLMode(FTPConnection.AUTH_TLS_FTP_CONNECTION);  
    }
    
    private void negotiateAndLogin(String authCommand) throws IOException,AuthenticationNotSupportedException
    {
    	
        Reply reply = sendCommand(new Command(authCommand));
        reply.dumpReply(System.out);
        if (ReplyCode.isPositiveCompletionReply(reply)) {
            try
            {
                socketProvider.negotiate();
            }catch (Exception e)
            {
                log.error(e,e);
            }
            (sendCommand(new Command(Command.USER, getUser()))).dumpReply(System.out);

            if (getPassword() != null && getPassword().length() > 0)
                (sendCommand(new Command(Command.PASS, getPassword()))).dumpReply(System.out);
            if (getAccount() != null && getAccount().length() > 0)
                (sendCommand(new Command(Command.ACCT, getAccount()))).dumpReply(System.out);
        } else {
            throw new AuthenticationNotSupportedException(authCommand
                    + " not supported by server");
        }
    }
}
