package org.ftp4che.examples;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.ftp4che.FTPConnection;
import org.ftp4che.FTPConnectionFactory;
import org.ftp4che.exception.ConfigurationException;
import org.ftp4che.exception.NotConnectedException;
import org.ftp4che.util.ftpfile.FTPFile;

public class FxpDirectoryTest {
    public static void main(String args[])
    {
        Logger log = Logger.getLogger("MAIN");
        
        Properties ptSource = new Properties();
        ptSource.setProperty("connection.host","172.25.13.149");
        ptSource.setProperty("connection.port","21");
        ptSource.setProperty("user.login","ftpuser");
        ptSource.setProperty("user.password","ftp4che");
        ptSource.setProperty("connection.type", "FTP_CONNECTION");
        ptSource.setProperty("connection.timeout", "10000");
        ptSource.setProperty("connection.passive", "true");
        
        Properties ptDest = new Properties();
        ptDest.setProperty("connection.host","172.25.12.197");
        ptDest.setProperty("connection.port","23");
        ptDest.setProperty("user.login","ftpuser");
        ptDest.setProperty("user.password","ftp4che");
        ptDest.setProperty("connection.type", "FTP_CONNECTION");
        ptDest.setProperty("connection.timeout", "10000");
        ptDest.setProperty("connection.passive", "true");

        try
        {
            FTPConnection src = FTPConnectionFactory.getInstance(ptSource);
            FTPConnection dst = FTPConnectionFactory.getInstance(ptDest);
            
            FTPFile srcDir = new FTPFile("/", "download", true);
            FTPFile dstDir = new FTPFile("/", "test", true);
            
            try
            {
                src.connect();
                dst.connect();
                
                src.fxpDirectory(dst, srcDir, dstDir);
                
                src.disconnect();
                dst.disconnect();
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
//            
//            try {
//            	ptSource.setProperty("connection.type", "IMPLICIT_SSL_FTP_CONNECTION");
//            	ptSource.setProperty("connection.port","990");
//            	src = FTPConnectionFactory.getInstance(ptSource);
//                src.connect();
//                src.disconnect();
//            }catch (NotConnectedException nce)
//            {
//                log.error(nce);
//            }
//            catch (IOException ioe)
//            {
//                log.error(ioe);
//            }
//            catch (Exception e)
//            {
//                log.error(e);
//            }
//            
//            try {
//            	ptSource.setProperty("connection.type", "IMPLICIT_TLS_FTP_CONNECTION");
//            	ptSource.setProperty("connection.port","990");
//            	src = FTPConnectionFactory.getInstance(ptSource);
//                src.connect();
//                src.disconnect();
//            }catch (NotConnectedException nce)
//            {
//                log.error(nce);
//            }
//            catch (IOException ioe)
//            {
//                log.error(ioe);
//            }
//            catch (Exception e)
//            {
//                log.error(e);
//            }
//            
//            try {
//            	ptSource.setProperty("connection.port","21");
//            	ptSource.setProperty("connection.type", "AUTH_SSL_FTP_CONNECTION");
//            	src = FTPConnectionFactory.getInstance(ptSource);
//                src.connect();
//                src.disconnect();
//            }catch (NotConnectedException nce)
//            {
//                log.error(nce);
//            }
//            catch (IOException ioe)
//            {
//                log.error(ioe);
//            }
//            catch (Exception e)
//            {
//                log.error(e);
//            }
//            
//            try {
//            	ptSource.setProperty("connection.type", "AUTH_TLS_FTP_CONNECTION");
//            	src = FTPConnectionFactory.getInstance(ptSource);
//                src.connect();
//                src.disconnect();
//            }catch (NotConnectedException nce)
//            {
//                log.error(nce);
//            }
//            catch (IOException ioe)
//            {
//                log.error(ioe);
//            }
//            catch (Exception e)
//            {
//                log.error(e);
//            }
        }catch(ConfigurationException ce)
        {
            log.error(ce);
        }
    }
}