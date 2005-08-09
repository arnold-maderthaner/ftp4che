/*
 * Created on 11.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ftp4che.exception;

import org.ftp4che.reply.ReplyCode;

/**
 * @author arnold
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NotConnectedException extends FtpIOException {
    public NotConnectedException(String description)
    {
        super(ReplyCode.REPLY_530,description);
    }

}
