package org.ftp4che.reply;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ftp4che.exception.FtpIOException;
import org.ftp4che.exception.FtpWorkflowException;
import org.ftp4che.exception.NotConnectedException;

public class Reply {
    List<String> lines = new ArrayList<String>();
    Logger log = Logger.getLogger(Reply.class.getName());
    
    public Reply(List<String> lines)
    {
        setLines(lines);
    }
   
    /**
     * @return Returns the lines.
     */
    public List<String> getLines() {
        return lines;
    }

    /**
     * @param lines The lines to set.
     */
    public void setLines(List<String> lines) {
        this.lines = new ArrayList<String>(lines);
    }
    
    public void dumpReply(OutputStream out)
    {
        try
        {
            for(String line: lines)
            {
                out.write(line.getBytes());
            }
        }catch (IOException ioe)
        {
            log.error("Couldn't dump reply",ioe);
        }
    }
    
    public String getReplyCode() {
    	return getLines().get( getLines().size() - 1 ).substring(0,3);
    }
    
    public String getReplyMessage() {
    	return getLines().get( getLines().size() - 1 ).substring(3);
    }
    
    public void validate() throws FtpWorkflowException,FtpIOException
    {
        if(ReplyCode.isPermanentNegativeCompletionReply(this))
        {
            if(getReplyCode().intern() == ReplyCode.REPLY_530.intern())
                throw new NotConnectedException("Not logged in");
            throw new FtpWorkflowException(this.getReplyCode(),this.getReplyMessage());
        }
        else if(ReplyCode.isTransientNegativeCompletionReply(this))
        {
            throw new FtpIOException(this.getReplyCode(),this.getReplyMessage());
        }
    }
    
   
}
