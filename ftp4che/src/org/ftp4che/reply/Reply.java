package org.ftp4che.reply;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public abstract class Reply {
    List<String> lines = new ArrayList<String>();
    Logger log = Logger.getLogger(Reply.class.getName());
    
    public Reply(List lines)
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
    public void setLines(List lines) {
        this.lines = new ArrayList(lines);
    }
    
    public abstract void process();

    
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
}
