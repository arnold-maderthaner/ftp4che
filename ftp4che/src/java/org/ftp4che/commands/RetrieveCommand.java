/**                                                                         *
*  This file is part of ftp4che.                                            *
*                                                                           *
*  This library is free software; you can redistribute it and/or modify it  *
*  under the terms of the GNU General Public License as published    		*
*  by the Free Software Foundation; either version 2 of the License, or     *
*  (at your option) any later version.                                      *
*                                                                           *
*  This library is distributed in the hope that it will be useful, but      *
*  WITHOUT ANY WARRANTY; without even the implied warranty of               *
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU        *
*  General Public License for more details.                          		*
*                                                                           *
*  You should have received a copy of the GNU General Public		        *
*  License along with this library; if not, write to the Free Software      *
*  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA  *
*                                                                           *
*****************************************************************************/
package org.ftp4che.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.ftp4che.io.ReplyWorker;
import org.ftp4che.reply.Reply;
import org.ftp4che.util.ftpfile.FTPFile;

public class RetrieveCommand extends DataConnectionCommand {

    private FTPFile fromFile;
    private FTPFile toFile;
    //TODO: throw Exception if fromFile not Exists
    
    public RetrieveCommand( String command, FTPFile fromFile )
    {
       super(command, fromFile.getName() );
       setFromFile( fromFile );
    }

    public RetrieveCommand( String command, FTPFile fromFile, FTPFile toFile )
    {
       super(command, fromFile.toString() );
       setFromFile( fromFile );
       setToFile ( toFile );
    }
    
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
    public FTPFile getToFile() {
        return toFile;
    }
    /**
     * @param toFile The toFile to set.
     */
    public void setToFile(FTPFile toFile) {
        this.toFile = toFile;
    }
  
}
