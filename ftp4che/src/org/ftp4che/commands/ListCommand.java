package org.ftp4che.commands;

import org.ftp4che.reply.Reply;
import org.ftp4che.util.ReplyWorker;
import org.ftp4che.util.SocketProvider;

public class ListCommand extends Command implements DataConnectionCommand {
	SocketProvider dataSocket;
	
    public ListCommand(String parameter)
    {
        super(Command.LIST,parameter);
    }
   
    public ListCommand()
    {
        this(".");
    }
    
    //TODO: what todo if you get exception from replyworker ?
    public Reply fetchDataConnectionReply() {
        ReplyWorker worker = new ReplyWorker(getDataSocket(),this);
        worker.start();
        while(worker.getStatus() == ReplyWorker.UNKNOWN)
        {
        	try
        	{
        		Thread.sleep(20);
        	}catch (InterruptedException ie) {}
        }
        if(worker.getStatus() == ReplyWorker.FINISHED)
        {
        	return worker.getReply();
        }
        else
        	return null;
        	
    }

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
