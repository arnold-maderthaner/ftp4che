/*
 * Created on 11.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ftp4che.examples;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.ftp4che.FTPConnection;
import org.ftp4che.FTPConnectionFactory;
import org.ftp4che.exception.ConfigurationException;
import org.ftp4che.exception.NotConnectedException;
import org.ftp4che.util.FTPFile;

/**
 * @author arnold
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class SimpleFTPLogin {
    public static void main(String args[])
    {
        Logger log = Logger.getLogger("MAIN");
        
        Properties pt = new Properties();
        pt.setProperty("connection.host","192.168.0.1");
        pt.setProperty("connection.port","21");
        pt.setProperty("user.login","ftpuser");
        pt.setProperty("user.password","ftp4che");
        pt.put("connection.type", new Integer(FTPConnection.FTP_CONNECTION));
        pt.put("connection.timeout",new Long(10000));
        pt.put("connection.passive",new Boolean(true));
        try
        {
            FTPConnection connection = FTPConnectionFactory.getInstance(pt);
            log.debug("user:" + connection.getUser());
            try
            {
                connection.connect();
                connection.getWorkDirectory();

                connection.makeDirectory("testdir");
                connection.changeDirectory("testdir");
                connection.getWorkDirectory();    
                
                connection.changeToParentDirectory();
                
                connection.removeDirectory("testdir");
                
                connection.getWorkDirectory();
                
                connection.noOperation();
                connection.changeDirectory("/home/ftpuser/download");
                List fileList = connection.getDirectoryListing();
                for(int i = 0; i < fileList.size(); i++)
                {
                    log.info("Name:" + ((FTPFile)fileList.get(i)).getName());
                    log.info("Mode:" + ((FTPFile)fileList.get(i)).getMode());
                    log.info("Date:" + ((FTPFile)fileList.get(i)).getDate());
                    log.info("Size:" + ((FTPFile)fileList.get(i)).getSize());
                }
                
                connection.disconnect();
            }catch (NotConnectedException nce)
            {
                log.error(nce);
            }
            catch (IOException ioe)
            {
                log.error(ioe);
            }
            catch (Exception e)
            {
                log.error(e);
            }
        }catch(ConfigurationException ce)
        {
            log.error(ce);
        }
    }

}
