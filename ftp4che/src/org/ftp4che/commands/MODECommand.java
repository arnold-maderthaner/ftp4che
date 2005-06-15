/**
 * Created on 11.06.2005
 * @author kurt
 */
package org.ftp4che.commands;

import java.util.ArrayList;
import java.util.List;

import org.ftp4che.Command;
import org.ftp4che.Result;

public class MODECommand implements Command {
    Result result[] = null;
    String modeCode;
    
    public MODECommand( String modeCode ) {
    	setModeCode( modeCode );
    }
    
	public void setResult(Result[] result) {
		this.result = result;
	}

	public Result[] getResult() {
		return this.result;
	}

	public String getModeCode() {
		return this.modeCode;
	}

	public void setModeCode( String modeCode ) {
		this.modeCode = modeCode;
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
        commands.add("MODE " + getModeCode() + Command.delimiter);
        
        return commands;
	}
}
