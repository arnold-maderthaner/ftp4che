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
package org.ftp4che.impl;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.ftp4che.FTPConnection;
import org.ftp4che.commands.Command;
import org.ftp4che.exception.AuthenticationNotSupportedException;
import org.ftp4che.exception.NotConnectedException;
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
    public void connect() throws NotConnectedException, IOException,AuthenticationNotSupportedException {
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
                socketProvider.socket().setSoTimeout(getTimeout());
                socketProvider.socket().setKeepAlive(true);
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
