/*
 * Created on 11.06.2005
 * @author arnold, kurt
 */
package org.ftp4che;

import java.util.List;


public interface Command {
    //TODO: add a CommandImpl to avoid duplicate code.
    //TODO: throw Error in CommandImpl.getTextCommands() to avoid wrong implementation
    
    public static final String delimiter = "\r\n";
    public void setResult(Result[] result);
    public Result[] getResult();
    public boolean isError();
    public boolean isFailure();
    public boolean isSuccess();
    //Used to build the specific command
    public List getTextCommands();
}
