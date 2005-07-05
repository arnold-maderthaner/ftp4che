package org.ftp4che.commands;

import java.io.File;

import org.ftp4che.reply.Reply;
import org.ftp4che.util.FTPFile;
import org.ftp4che.util.ReplyWorker;

public class RetrieveCommand extends DataConnectionCommand {

    private FTPFile fromFile;
    private File toFile;
    //TODO: throw Exception if fromFile not Exists
    
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
    
    public Reply fetchDataConnectionReply() throws Exception {
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
    	   throw worker.getCaughtException();
       }
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
  
}
