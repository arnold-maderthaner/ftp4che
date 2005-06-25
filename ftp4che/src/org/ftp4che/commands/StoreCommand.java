package org.ftp4che.commands;

import java.io.File;

import org.ftp4che.reply.Reply;
import org.ftp4che.util.FTPFile;

public class StoreCommand extends Command implements DataConnectionCommand {
    
    FTPFile toFile;
    File fromFile;
    
    public StoreCommand(String command, FTPFile toFile)
    {
       super(command, toFile.getName() );
       setToFile( toFile );
    }
    
    public StoreCommand(String command, File fromFile, FTPFile toFile)
    {
       super(command, toFile.getName() );
       setToFile( toFile );
       setFromFile( fromFile );
       
    }
    
    public Reply fetchDataConnectionReply() {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * @return Returns the file.
     */
    public FTPFile getToFile() {
        return toFile;
    }
    
    /**
     * @param file The file to set.
     */
    public void setToFile(FTPFile file) {
        this.toFile = file;
    }

    /**
     * @return Returns the fromFile.
     */
    public File getFromFile() {
        return fromFile;
    }

    /**
     * @param fromFile The fromFile to set.
     */
    public void setFromFile(File fromFile) {
        this.fromFile = fromFile;
    }
}
