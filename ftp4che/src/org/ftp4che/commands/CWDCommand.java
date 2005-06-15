/**
 * Created on 11.06.2005
 * @author kurt
 */
package org.ftp4che.commands;

import java.util.ArrayList;
import java.util.List;

import org.ftp4che.Command;
import org.ftp4che.Result;

public class CWDCommand implements Command {
    Result result[] = null;
    String directory;
    
    public CWDCommand( String directory ) {
    	setDirectory( directory );
    }
    
	public void setResult(Result[] result) {
		this.result = result;
	}

	public Result[] getResult() {
		return this.result;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}
	
	public boolean isError() {
		return false;
	}

	public boolean isFailure() {
		return false;
	}

	public boolean isSuccess() {
		return false;
	}

	public List getTextCommands() {
        List commands = new ArrayList();
        commands.add("CWD " + getDirectory() + Command.delimiter);
        
        return commands;
	}
}
