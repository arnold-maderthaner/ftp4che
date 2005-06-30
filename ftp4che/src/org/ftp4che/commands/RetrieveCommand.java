package org.ftp4che.commands;

import java.io.File;

import org.ftp4che.reply.Reply;
import org.ftp4che.util.FTPFile;
import org.ftp4che.util.ReplyWorker;
import org.ftp4che.util.SocketProvider;

public class RetrieveCommand extends Command implements DataConnectionCommand {
	SocketProvider dataSocket;
    private FTPFile fromFile;
    private File toFile;
    
    public RetrieveCommand( String command, FTPFile fromFile )
    {
       super(command, fromFile.getName() );
       setFromFile( fromFile );
    }

    public RetrieveCommand( String command, FTPFile fromFile, File toFile )
    {
       super(command, fromFile.getName() );
       setFromFile( fromFile );
       setToFile ( toFile );
    }
    
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
     * @return Returns the file.
     */
    public FTPFile getFromFile() {
        return fromFile;
    }
    
    /**
     * @param file The file to set.
     */
    public void setFromFile(FTPFile file) {
        this.fromFile = file;
    }
    /**
     * @return Returns the toFile.
     */
    public File getToFile() {
        return toFile;
    }
    /**
     * @param toFile The toFile to set.
     */
    public void setToFile(File toFile) {
        this.toFile = toFile;
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
