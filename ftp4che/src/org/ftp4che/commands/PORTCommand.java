/**
 * Created on 11.06.2005
 * @author kurt
 */
package org.ftp4che.commands;

import java.util.ArrayList;
import java.util.List;

import org.ftp4che.Command;
import org.ftp4che.Result;

public class PORTCommand implements Command {
    Result result[] = null;
    String hostPort;
    
    public PORTCommand( String hostPort ) {
    	setHostPort( hostPort );
    }
    
	public void setResult(Result[] result) {
		this.result = result;
	}

	public Result[] getResult() {
		return this.result;
	}

	public String getHostPort() {
		return this.hostPort;
	}

	public void setHostPort(String hostPort) {
		this.hostPort = hostPort;
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
        commands.add("PORT " + getHostPort() + Command.delimiter);
        
        return commands;
	}
}
