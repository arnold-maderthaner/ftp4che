/*
 * Created on 11.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ftp4che.examples;

import java.io.File;
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
        pt.setProperty("connection.host","172.25.13.149");
        pt.setProperty("connection.port","21");
        pt.setProperty("user.login","ftpuser");
        pt.setProperty("user.password","ftp4che");
        pt.setProperty("connection.type", "FTP_CONNECTION");
        pt.setProperty("connection.timeout", "10000");
        pt.setProperty("connection.passive", "true");
        pt.setProperty("connection.proxy_type", "SOCKS4");
        pt.setProperty("connection.proxy_host", "127.0.0.1");
        pt.setProperty("connection.proxy_port", "1080");
        pt.setProperty("connection.proxy_user", "kurt");
        
        try
        {
            FTPConnection connection = FTPConnectionFactory.getInstance(pt);
            log.debug("user:" + connection.getUser());
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
        }catch(ConfigurationException ce)
        {
            log.error(ce);
        }
    }
}