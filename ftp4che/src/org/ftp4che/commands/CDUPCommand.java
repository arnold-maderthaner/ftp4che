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
public class CDUPCommand implements Command {
    Result result[] = null;
	
	public void setResult(Result[] result) {
		this.result = result;
	}

	public Result[] getResult() {
		return this.result;
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
        commands.add("CDUP" + Command.delimiter);
        
        return commands;
	}
}
