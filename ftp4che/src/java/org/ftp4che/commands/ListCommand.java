package org.ftp4che.commands;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.ftp4che.reply.Reply;
import org.ftp4che.util.ReplyWorker;

public class ListCommand extends DataConnectionCommand {
	
    public ListCommand(String parameter)
    {
        super(Command.LIST,parameter);
    }
   
    public ListCommand()
    {
        this(".");
    }
    
    //TODO: what todo if you get exception from replyworker ?
    public Reply fetchDataConnectionReply() throws FileNotFoundException,IOException{
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
        {
            if(worker.getCaughtException() instanceof FileNotFoundException)
                throw (FileNotFoundException)worker.getCaughtException();
            else
                throw (IOException)worker.getCaughtException();        
        }
        	
    }

}
