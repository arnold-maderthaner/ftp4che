/**
 * Created on 11.06.2005
 * @author kurt
 */
package org.ftp4che.commands;

import java.util.ArrayList;
import java.util.List;

import org.ftp4che.Command;
import org.ftp4che.Result;

public class TYPECommand implements Command {
    Result result[] = null;
    String typeCode;
    
    public TYPECommand( String typeCode ) {
    	setTypeCode( typeCode );
    }
    
	public void setResult(Result[] result) {
		this.result = result;
	}

	public Result[] getResult() {
		return this.result;
	}

	public String getTypeCode() {
		return this.typeCode;
	}

	public void setTypeCode( String typeCode ) {
		this.typeCode = typeCode;
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
        commands.add("TYPE " + getTypeCode() + Command.delimiter);
        
        return commands;
	}
}
