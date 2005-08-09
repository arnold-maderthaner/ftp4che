package org.ftp4che.exception;

import org.ftp4che.reply.ReplyCode;

public class FtpFileNotFoundException extends FtpWorkflowException {

    public FtpFileNotFoundException(String description)
    {
        super(ReplyCode.REPLY_550,description);
    }
}
