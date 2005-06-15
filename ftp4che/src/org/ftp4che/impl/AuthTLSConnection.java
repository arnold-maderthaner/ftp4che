/*
 * Created on 11.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ftp4che.impl;

import java.io.IOException;

import org.ftp4che.Command;
import org.ftp4che.FTPConnection;
import org.ftp4che.Result;
import org.ftp4che.exception.NotConnectedException;

/**
 * @author arnold
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AuthTLSConnection extends SuperFTPConnection implements FTPConnection {
    public AuthTLSConnection()
    {
        super();
    }

    /* (non-Javadoc)
     * @see org.ftp4che.FTPConnection#connect()
     */
    public void connect() throws NotConnectedException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.ftp4che.FTPConnection#login()
     */
    public void login() throws IOException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.ftp4che.FTPConnection#connectAndLogin()
     */
    public void connectAndLogin() throws NotConnectedException, IOException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.ftp4che.FTPConnection#disconnect()
     */
    public void disconnect() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.ftp4che.FTPConnection#sendCommand(org.ftp4che.Command)
     */
    public Result[] sendCommand(Command cmd) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.ftp4che.FTPConnection#getConnectionStatus()
     */
    public int getConnectionStatus() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.ftp4che.FTPConnection#setTimeout(long)
     */
    public void setTimeout(long millis) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.ftp4che.FTPConnection#changeDirectory(java.lang.String)
     */
    public void changeDirectory(String directory) throws IOException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.ftp4che.FTPConnection#getWorkDirectory()
     */
    public String getWorkDirectory() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.ftp4che.FTPConnection#getParentDirectory()
     */
    public String changeToParentDirectory() throws IOException {
        // TODO Auto-generated method stub
        return "";
    }

    /* (non-Javadoc)
     * @see org.ftp4che.FTPConnection#makeDirectory(java.lang.String)
     */
    public boolean makeDirectory(String pathname) throws IOException {
        // @todo Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.ftp4che.FTPConnection#removeDirectory(java.lang.String)
     */
    public boolean removeDirectory(String pathname) throws IOException {
        // @todo Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.ftp4che.FTPConnection#noOperation()
     */
    public void noOperation() throws IOException {
        // @todo Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.ftp4che.FTPConnection#setPassiveMode()
     */
    public boolean sendPassiveMode() throws IOException {
        // @todo Auto-generated method stub
        return false;
    }
}
