/*
 * Created on 11.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ftp4che;

import java.net.InetSocketAddress;
import java.util.Properties;

import org.ftp4che.exception.ConfigurationException;
import org.ftp4che.impl.AuthSSLFTPConnection;
import org.ftp4che.impl.AuthTLSFTPConnection;
import org.ftp4che.impl.ImplicitSSLFTPConnection;
import org.ftp4che.impl.NormalFTPConnection;

/**
 * @author arnold
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FTPConnectionFactory {
    
    
    /**
     * This factory should be called to get you a new FTPConnection
     * You can set the connection information with a properties object.
     * @param pt you have to set the connection informations:
     * connection.host = hostname to the server you want to connect (String)
     * connection.port = port you want to connect to (String)
     * user.login = login name (String) 
     * user.password = password (Sring). this parameter is optional
     * user.account = Account Information (String). This parameter is optional
     * connection.type = The connection you want to have (normal,auth ssl,auth tls,...). There are constants (int primitiv type) in FTPConnection. You have to give a Integer object.
     * connection.timeout = The timeout that will be used (Long object)
     * connection.passive = Should the DataConnection be established in passive mode (Boolean Object)
     * @return FTPConnection the ftpconnection. you can than do a connect() and login() to connect and login to the server
     * @throws ConfigurationException will be thrown if a parameter is missing or invalid
     * @author arnold,kurt
     */
    public static FTPConnection getInstance(Properties pt) throws ConfigurationException
    {
        //TODO: to make this more 1.5 like we could use autoboxing (if we don't want a 1.4 compatible build)
        return FTPConnectionFactory.getInstance(pt.getProperty("connection.host"),
                                                Integer.parseInt("connection.port"),
                                                pt.getProperty("user.login"),
                                                pt.getProperty("user.password"),
                                                pt.getProperty("user.account"),
                                                Long.parseLong(pt.getProperty("connection.timeout")),
                                                Integer.parseInt(pt.getProperty("connection.type")),
                                                Boolean.parseBoolean(pt.getProperty("connection.passive")));
    }

    /**
     * This factory should be called to get you a new FTPConnection
     * @param host = hostname to the server you want to connect 
     * @param port = port you want to connect to
     * @param user = login name 
     * @param password = password. this parameter is optional
     * @param account = Account Information. This parameter is optional
     * @param connectionType = The connection you want to have (normal,auth ssl,auth tls,...). There are constants (int primitiv type) in FTPConnection.
     * @param timeout = The timeout that will be used
     * @param passiveMode = Should the DataConnection be established in passive mode (Boolean Object)
     * @return FTPConnection the ftpconnection. you can than do a connect() and login() to connect and login to the server
     * @throws ConfigurationException will be thrown if a parameter is missing or invalid
     * @author arnold,kurt
     */
    public static FTPConnection getInstance(String host,int port,String user,String password,String account,long timeout,int connectionType,boolean passiveMode) throws ConfigurationException
    {
        FTPConnection connection = null;
        if(connectionType == FTPConnection.FTP_CONNECTION)
        {
            connection = new NormalFTPConnection();
        }
        else if(connectionType == FTPConnection.IMPLICIT_SSL_FTP_CONNECTION)
        {
           	connection = new ImplicitSSLFTPConnection();
        }
        else if(connectionType == FTPConnection.AUTH_SSL_FTP_CONNECTION)
        {
            connection = new AuthSSLFTPConnection();
        }
        else if(connectionType == FTPConnection.AUTH_TLS_FTP_CONNECTION)
        {
            connection = new AuthTLSFTPConnection();
        }
        else
        {
            throw new ConfigurationException("No or unknown connection.type in properties");
        }
        connection.setAddress(new InetSocketAddress(host,port));
        connection.setUser(user);
        connection.setPassword(password);
        connection.setAccount(account);
        connection.setTimeout(timeout);
        connection.setPassiveMode(passiveMode);
        return connection;
    }

}
