/**                                                                         
 *  This file is part of ftp4che.                                            
 *  This library is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as published
 *  by the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.ftp4che.util.ftpfile;

/**
 * @author Geetu Preet Sandhu, Gaurav Sharma
 * Adding support to MVS FTPS server.
 */

import java.text.ParseException;
import java.util.Locale;

import org.apache.log4j.Logger;

public class MVSFileParser implements FileParser {
    private static final Logger log = Logger.getLogger(MVSFileParser.class);

    private Locale locale;
    
    public MVSFileParser(Locale locale) {
        this.locale = locale;
    }

    public FTPFile parse(String serverString, String parentDirectory)
            throws ParseException {
        FTPFile file = null;
        String batchNumber = "";
        String batchIdValue = "";
        String bid = "";
        String size = "";
        String ct = "";
        if(serverString != null) {
            
            batchNumber = serverString.substring(serverString.indexOf("#"),serverString.indexOf(" ",serverString.indexOf("#")));
            bid = serverString.substring(serverString.indexOf("BID"),serverString.indexOf(" ",serverString.indexOf("BID")));
            ct = serverString.substring(serverString.indexOf("CT"),serverString.indexOf(" ",serverString.indexOf("CT")));
            batchIdValue = bid.substring(bid.indexOf("=")+1).trim();
            size = ct.substring(ct.indexOf("=")+1).trim();
            
            if(!parentDirectory.startsWith("/")) {
            	parentDirectory = "/" + parentDirectory;
            }
            if(!parentDirectory.endsWith("/")) {
            	parentDirectory = parentDirectory + "/";
            }
            file = new FTPFile(FTPFile.MVS, parentDirectory, batchIdValue.trim(),
                    serverString);
            long fileSize = Long.parseLong(size)/1024;
            file.setSize(fileSize);
            file.setLinkedName(batchNumber);
     
        }
        return file;
    }

}
