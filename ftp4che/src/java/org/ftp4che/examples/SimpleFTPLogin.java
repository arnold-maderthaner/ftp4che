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
        pt.setProperty("connection.host","127.0.0.1");
        pt.setProperty("connection.port","54322");
        pt.setProperty("user.login","ftpuser");
        pt.setProperty("user.password","ftp4che");
        pt.put("connection.type", new Integer(FTPConnection.FTP_CONNECTION));
        pt.put("connection.timeout",new Integer(10000));
        pt.put("connection.passive",new Boolean(true));
        pt.put("connection.downloadbw", 2500000); // 10KB/s
        pt.put("connection.uploadbw", Integer.MAX_VALUE); // 10KB/s
        
        try
        {
            FTPConnection connection = FTPConnectionFactory.getInstance(pt);
//            FTPConnection connection2 = FTPConnectionFactory.getInstance(pt);
            log.debug("user:" + connection.getUser());
            try
            {
                connection.connect();
//                connection2.connect();
//                List<String> list = connection.getFastDirectoryListing();
//                for(String s : list)
//                {
//                    log.debug("FastList: " + s);
//                }
                connection.getWorkDirectory();
                connection.getDirectoryListing();
                connection.makeDirectory("testdir");
                connection.changeDirectory("testdir");
                log.debug("Working Directory: " + connection.getWorkDirectory());    
              
                connection.changeToParentDirectory();
                
                connection.removeDirectory("testdir");
                
                log.debug("Working Directory: " + connection.getWorkDirectory());
               
                connection.noOperation();
                connection.changeDirectory("/home/ftpuser/download");

                connection.getWorkDirectory();
                log.debug("Working Directory: " + connection.getWorkDirectory());    
                

                List<FTPFile> fileList = connection.getDirectoryListing();
                for(int i = 0; i < fileList.size(); i++)
                    log.info("Name:" + fileList.get(i).getName() + " Mode:" + fileList.get(i).getMode() + " Date:" + fileList.get(i).getDate() + " Size:" + fileList.get(i).getSize());
                log.debug("List Size:" + fileList.size());


                connection.changeDirectory("/home/ftpuser/download");
                FTPFile fromFile = new FTPFile();
                fromFile.setPath("/home/ftpuser/download/");
                fromFile.setName("50mb");
                long start = System.currentTimeMillis();
                connection.downloadFile(fromFile,new FTPFile("/tmp/", "50mb_limited"));
                log.debug("kb/sec: " + ((double)50060 / (System.currentTimeMillis() - start)*1000));
//                connection.changeDirectory("/home/ftpuser/upload");
//                connection.uploadFile(new File("/home/ftpuser/download" + File.separator + "testfile1.doc"),new FTPFile("testfile1.doc"));
//                connection.changeDirectory("/home/ftpuser/download");
                
//              FTPFile fromFile = new FTPFile();
//              fromFile.setPath("/home/ftpuser/download/");
//              fromFile.setName("1mb");
//              
//              FTPFile toFile = new FTPFile();
//              toFile.setPath("/home/ftpuser/upload/");
//              toFile.setName("1mb_1");
                
//                connection.changeDirectory("/home/ftpuser/download/");
//                connection2.changeDirectory("/home/ftpuser/upload/");
                
//                connection.getWorkDirectory();
//                connection2.getWorkDirectory();
                
//                connection.fxpFile(connection2, fromFile, toFile);
                
                connection.disconnect();
//                connection2.disconnect();
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