package org.ftp4che.reply;

import java.util.ArrayList;
import java.util.List;

public abstract class Reply {
    List lines = new ArrayList();
    
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
    
    public abstract void process();

}
