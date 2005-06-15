/**
 * Created on 11.06.2005
 * @author kurt
 */
package org.ftp4che.commands;

import java.util.ArrayList;
import java.util.List;

import org.ftp4che.Command;
import org.ftp4che.Result;

public class MKDCommand implements Command {
    Result result[] = null;
    String pathname;
    
    public MKDCommand( String pathname ) {
    	setPathname( pathname );
    }
    
	public void setResult(Result[] result) {
		this.result = result;
	}

	public Result[] getResult() {
		return this.result;
	}

	public String getPathname() {
		return this.pathname;
	}

	public void setPathname( String pathname ) {
		this.pathname = pathname;
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
        commands.add("MKD " + getPathname() + Command.delimiter);
        
        return commands;
	}
}
