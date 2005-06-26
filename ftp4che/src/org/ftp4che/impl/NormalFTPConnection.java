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
import org.ftp4che.commands.Command;
import org.ftp4che.exception.NotConnectedException;
import org.ftp4che.reply.Reply;
import org.ftp4che.util.ReplyWorker;
import org.ftp4che.util.SocketProvider;

/**
 * @author arnold
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NormalFTPConnection extends FTPConnection {
    
    Logger log = Logger.getLogger(NormalFTPConnection.class.getName());
    
    public NormalFTPConnection()
    {
        super();
    }
    
    @Override
    public void connect() throws NotConnectedException, IOException {
        try
        {
            socketProvider = new SocketProvider();
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
        (ReplyWorker.readReply(socketProvider)).dumpReply(System.out);
        (sendCommand(new Command(Command.USER,getUser()))).dumpReply(System.out);
        if(getPassword() != null && getPassword().length() > 0)
           (sendCommand(new Command(Command.PASS,getPassword()))).dumpReply(System.out);
        if(getAccount() != null && getAccount().length() > 0)
            (sendCommand(new Command(Command.ACCT,getAccount()))).dumpReply(System.out);
    }
}
