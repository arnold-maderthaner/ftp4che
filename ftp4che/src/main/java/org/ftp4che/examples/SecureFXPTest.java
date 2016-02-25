package org.ftp4che.examples;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.ftp4che.FTPConnection;
import org.ftp4che.FTPConnectionFactory;
import org.ftp4che.exception.ConfigurationException;
import org.ftp4che.exception.FtpIOException;
import org.ftp4che.exception.FtpWorkflowException;
import org.ftp4che.exception.NotConnectedException;
import org.ftp4che.util.ftpfile.FTPFile;

public class SecureFXPTest {
	
	public static void main(String args[]) throws IOException, FtpIOException, FtpWorkflowException
	{
		
		 Logger log = Logger.getLogger("MAIN");
		 log.setLevel(Level.DEBUG);
	        
	        Properties ptSource = new Properties();
	        ptSource.setProperty("connection.host","127.0.0.1");
	        ptSource.setProperty("connection.port","10000");
	        ptSource.setProperty("user.login","ftpuser");
	        ptSource.setProperty("user.password","ftp4che");
	        ptSource.setProperty("connection.type", "AUTH_TLS_FTP_CONNECTION");
	        ptSource.setProperty("connection.timeout", "10000");
	        ptSource.setProperty("connection.passive", "true");
	        
	        Properties ptDest = new Properties();
	        ptDest.setProperty("connection.host","127.0.0.1");
	        ptDest.setProperty("connection.port","54223");
	        ptDest.setProperty("user.login","ftpuser");
	        ptDest.setProperty("user.password","ftp4che");
	        ptDest.setProperty("connection.type", "AUTH_TLS_FTP_CONNECTION");
	        ptDest.setProperty("connection.timeout", "10000");
	        ptDest.setProperty("connection.passive", "true");

	        try
	        {
	            FTPConnection src = FTPConnectionFactory.getInstance(ptSource);
	            FTPConnection dst = FTPConnectionFactory.getInstance(ptDest);
	           
	            FTPFile srcDir = new FTPFile("/", "ZIP", true);
	            FTPFile dstDir = new FTPFile("/", "ARNOLD_ZIP", true);
	            
	            try
	            {
	                src.connect();
	                dst.connect();
	                src.setSscnSupport(true);
	                src.setCpsvSupport(true);
		            dst.setSscnSupport(true);
		            dst.setCpsvSupport(true);
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
	        }catch(ConfigurationException ce)
	        {
	            log.error(ce);
	        }
	}

}
