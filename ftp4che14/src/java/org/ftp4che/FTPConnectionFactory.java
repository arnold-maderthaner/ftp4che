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
package org.ftp4che;

import java.net.InetSocketAddress;
import java.util.Properties;

import org.ftp4che.exception.ConfigurationException;
import org.ftp4che.impl.SecureFTPConnection;
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
                                                Integer.parseInt(pt.getProperty("connection.port")),
                                                pt.getProperty("user.login"),
                                                pt.getProperty("user.password"),
                                                pt.getProperty("user.account"),
                                                ((Integer)pt.get("connection.timeout")).intValue(),
                                                ((Integer)pt.get("connection.type")).intValue(),
                                                ((Boolean)pt.get("connection.passive")).booleanValue());
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
     * @param passiveMode = Should the DataConnection be established in passive mode
     * @return FTPConnection the ftpconnection. you can than do a connect() and login() to connect and login to the server
     * @throws ConfigurationException will be thrown if a parameter is missing or invalid
     * @author arnold,kurt
     */
    public static FTPConnection getInstance(String host,int port,String user,String password,String account,int timeout,int connectionType,boolean passiveMode) throws ConfigurationException
    {
        FTPConnection connection = null;
        if(connectionType == FTPConnection.FTP_CONNECTION)
        {
            connection = new NormalFTPConnection();
        }
        else if(connectionType == FTPConnection.AUTH_TLS_FTP_CONNECTION ||
                connectionType == FTPConnection.AUTH_SSL_FTP_CONNECTION ||
                connectionType == FTPConnection.IMPLICIT_SSL_FTP_CONNECTION)
        {
           	connection = new SecureFTPConnection();
        }
        else
        {
            throw new ConfigurationException("No or unknown connection.type in properties");
        }
        connection.setConnectionType(connectionType);
        connection.setAddress(new InetSocketAddress(host,port));
        connection.setUser(user);
        connection.setPassword(password);
        connection.setAccount(account);
        connection.setTimeout(timeout);
        connection.setPassiveMode(passiveMode);
        return connection;
    }
    
    
    /**
     * This factory should be called to get you a new FTPConnection
     * @param host = hostname to the server you want to connect 
     * @param port = port you want to connect to
     * @param user = login name 
     * @param password = password. this parameter is optional
     * @return FTPConnection the ftpconnection. you can than do a connect() and login() to connect and login to the server
     * @throws ConfigurationException will be thrown if a parameter is missing or invalid
     * @author arnold,kurt
     */
    public static FTPConnection getInstance(String host,int port,String user,String password) throws ConfigurationException
    {
        return FTPConnectionFactory.getInstance(host,port,user,password,null,10000,FTPConnection.FTP_CONNECTION,false);
    }

    /**
     * This factory should be called to get you a new FTPConnection
     * @param host = hostname to the server you want to connect 
     * @param port = port you want to connect to
     * @param user = login name 
     * @param password = password. this parameter is optional
     * @param account = Account Information. This parameter is optional
     * @param passiveMode = Should the DataConnection be established in passive mode
     * @return FTPConnection the ftpconnection. you can than do a connect() and login() to connect and login to the server
     * @throws ConfigurationException will be thrown if a parameter is missing or invalid
     * @author arnold,kurt
     */
    public static FTPConnection getInstance(String host,int port,String user,String password,boolean passive) throws ConfigurationException
    {
        return FTPConnectionFactory.getInstance(host,port,user,password,null,10000,FTPConnection.FTP_CONNECTION,passive);
    }
    
    /**
     * This factory should be called to get you a new FTPConnection
     * @param host = hostname to the server you want to connect 
     * @param port = port you want to connect to
     * @param user = login name 
     * @param password = password. this parameter is optional
     * @param connectionType = The connection you want to have (normal,auth ssl,auth tls,...). There are constants (int primitiv type) in FTPConnection.
     * @param passiveMode = Should the DataConnection be established in passive mode
     * @return FTPConnection the ftpconnection. you can than do a connect() and login() to connect and login to the server
     * @throws ConfigurationException will be thrown if a parameter is missing or invalid
     * @author arnold,kurt
     */
    public static FTPConnection getInstance(String host,int port,String user,String password,int connectionType, boolean passive) throws ConfigurationException
    {
        return FTPConnectionFactory.getInstance(host,port,user,password,null,10000,connectionType,passive);
    }
    
    /**
     * This factory should be called to get you a new FTPConnection
     * @param host = hostname to the server you want to connect 
     * @param user = login name 
     * @return FTPConnection the ftpconnection. you can than do a connect() and login() to connect and login to the server
     * @throws ConfigurationException will be thrown if a parameter is missing or invalid
     * @author arnold,kurt
     */
    public static FTPConnection getInstance(String host,String user) throws ConfigurationException
    {
        return FTPConnectionFactory.getInstance(host,21,user,"",null,10000,FTPConnection.FTP_CONNECTION,false);
    }
}
