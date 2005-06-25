package org.ftp4che.commands;

import java.io.File;

import org.ftp4che.reply.Reply;
import org.ftp4che.util.FTPFile;

public class RetrieveCommand extends Command implements DataConnectionCommand {

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
        // TODO Auto-generated method stub
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

}
