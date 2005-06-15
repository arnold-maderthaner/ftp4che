/*
 * Created on 11.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ftp4che.commands;

import java.util.ArrayList;
import java.util.List;

import org.ftp4che.Command;
import org.ftp4che.Result;

/**
 * @author arnold
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class QUITCommand implements Command {

    Result result[] = null;

    
    public void setResult(Result[] result) {
        this.result = result;
    }

    /* (non-Javadoc)
     * @see org.ftp4che.Command#getResult()
     */
    public Result[] getResult() {
        return result;
    }

    /* (non-Javadoc)
     * @see org.ftp4che.Command#isError()
     */
    public boolean isError() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.ftp4che.Command#isFailure()
     */
    public boolean isFailure() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.ftp4che.Command#isSuccess()
     */
    public boolean isSuccess() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.ftp4che.Command#getTextCommands()
     */
    public List getTextCommands() {
        List commands = new ArrayList();
        commands.add("QUIT" + Command.delimiter);
        return commands;
    }

}
