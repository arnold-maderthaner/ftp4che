package org.ftp4che.examples;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.ftp4che.FTPConnection;
import org.ftp4che.FTPConnectionFactory;
import org.ftp4che.exception.ConfigurationException;
import org.ftp4che.exception.NotConnectedException;

public class ConnectTest {
    public static void main(String args[])
    {
        Logger log = Logger.getLogger("MAIN");
        
        Properties pt = new Properties();
        pt.setProperty("connection.host","172.25.13.187");
        pt.setProperty("connection.port","21");
        pt.setProperty("user.login","ftpuser");
        pt.setProperty("user.password","ftp4che");
        pt.setProperty("connection.type", "FTP_CONNECTION");
        pt.setProperty("connection.timeout", "10000");
        pt.setProperty("connection.passive", "true");

        try
        {
            FTPConnection connection = FTPConnectionFactory.getInstance(pt);
            
            try
            {
                connection.connect();
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
            
            try {
            	pt.setProperty("connection.type", "IMPLICIT_SSL_FTP_CONNECTION");
            	pt.setProperty("connection.port","990");
            	connection = FTPConnectionFactory.getInstance(pt);
                connection.connect();
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
            
            try {
            	pt.setProperty("connection.type", "IMPLICIT_TLS_FTP_CONNECTION");
            	pt.setProperty("connection.port","990");
            	connection = FTPConnectionFactory.getInstance(pt);
                connection.connect();
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
            
            try {
            	pt.setProperty("connection.port","21");
            	pt.setProperty("connection.type", "AUTH_SSL_FTP_CONNECTION");
            	connection = FTPConnectionFactory.getInstance(pt);
                connection.connect();
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
            
            try {
            	pt.setProperty("connection.type", "AUTH_TLS_FTP_CONNECTION");
            	connection = FTPConnectionFactory.getInstance(pt);
                connection.connect();
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