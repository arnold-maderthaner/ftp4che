package org.ftp4che.reply;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.ftp4che.exception.FtpFileNotFoundException;
import org.ftp4che.exception.FtpIOException;
import org.ftp4che.exception.FtpWorkflowException;
import org.ftp4che.exception.NotConnectedException;

public class Reply {
    List lines = new ArrayList();
    Logger log = Logger.getLogger(Reply.class.getName());
    
    public Reply(List lines)
    {
        setLines(lines);
    }
   
    /**
     * @return Returns the lines.
     */
    public List getLines() {
        return lines;
    }

    /**
     * @param lines The lines to set.
     */
    public void setLines(List lines) {
        this.lines = new ArrayList(lines);
    }
    
    public void dumpReply(OutputStream out)
    {
        try
        {
            for(Iterator it = lines.iterator(); it.hasNext();)
            {
                out.write(((String)it.next()).getBytes());
            }
        }catch (IOException ioe)
        {
            log.error("Couldn't dump reply",ioe);
        }
    }
    
    public String getReplyCode() {
    	return ((String)getLines().get( getLines().size() - 1 )).substring(0,3);
    }
    
    public String getReplyMessage() {
    	return ((String)getLines().get( getLines().size() - 1 )).substring(4);
    }
    
    public void validate() throws FtpWorkflowException,FtpIOException
    {
        if(ReplyCode.isPermanentNegativeCompletionReply(this))
        {
            if(getReplyCode().intern() == ReplyCode.REPLY_530.intern())
                throw new NotConnectedException(getReplyMessage());
            if(getReplyCode().intern() == ReplyCode.REPLY_550.intern())
                throw new FtpFileNotFoundException(getReplyMessage());
            throw new FtpWorkflowException(this.getReplyCode(),this.getReplyMessage());
        }
        else if(ReplyCode.isTransientNegativeCompletionReply(this))
        {
            throw new FtpIOException(this.getReplyCode(),this.getReplyMessage());
        }
    }
    
   
}
