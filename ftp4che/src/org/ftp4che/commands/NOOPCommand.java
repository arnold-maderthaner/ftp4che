/**
 * Created on 11.06.2005
 * @author kurt
 */
package org.ftp4che.commands;

import java.util.ArrayList;
import java.util.List;

import org.ftp4che.Command;
import org.ftp4che.Result;

public class NOOPCommand implements Command {
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
        commands.add("NOOP" + Command.delimiter);
        
        return commands;
	}
}
