/**
 * Created on 11.06.2005
 * @author kurt
 */
package org.ftp4che.commands;

import java.util.ArrayList;
import java.util.List;

import org.ftp4che.Command;
import org.ftp4che.Result;

public class ALLOCommand implements Command {
    Result result[] = null;
    String size, maximumSize;
    
    public ALLOCommand( String size, String maximumSize ) {
    	setSize( size );
    	setMaximumSize( maximumSize );
    }
    
	public void setResult(Result[] result) {
		this.result = result;
	}

	public Result[] getResult() {
		return this.result;
	}

	public String getSize() {
		return this.size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getMaximumSize() {
		return this.maximumSize;
	}

	public void setMaximumSize(String maximumSize) {
		this.maximumSize = maximumSize;
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
        
        if( getMaximumSize() != null && getMaximumSize().length() > 0 )
            commands.add("CWD " + getSize() + " R " + getMaximumSize() + Command.delimiter);
        else
            commands.add("CWD " + getSize() + Command.delimiter);
        
        return commands;
	}
}
