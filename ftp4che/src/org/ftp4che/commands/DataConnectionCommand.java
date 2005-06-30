package org.ftp4che.commands;

import org.ftp4che.reply.Reply;
import org.ftp4che.util.SocketProvider;

public abstract class DataConnectionCommand extends Command{
	SocketProvider dataSocket;
	
	public DataConnectionCommand(String command)
	{
		super(command);
	}
	
	public DataConnectionCommand(String command,String parameter)
	{
		super(command,parameter);
	}
	
	public abstract Reply fetchDataConnectionReply();
    
	/**
	 * @return Returns the dataSocket.
	 */
	public SocketProvider getDataSocket() {
		return dataSocket;
	}

	/**
	 * @param dataSocket The dataSocket to set.
	 */
	public void setDataSocket(SocketProvider dataSocket) {
		this.dataSocket = dataSocket;
	}
}
