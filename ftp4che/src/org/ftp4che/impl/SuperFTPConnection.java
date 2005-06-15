/*
 * Created on 11.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ftp4che.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.List;


import org.ftp4che.commands.LoginCommand;
import org.ftp4che.exception.ConfigurationException;
import org.ftp4che.util.SocketProvider;

/**
 * @author arnold
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SuperFTPConnection extends Thread {
    InetSocketAddress address = null;
    String user = "";
    String password = "";
    String account = "";
    boolean passiveMode = false;
    long timeout = 10000;
    LoginCommand login = null;
//  Charset and decoder
//	Charset charset = Charset.defaultCharset();
    Charset charset = Charset.forName("ISO-8859-1");
	CharsetDecoder decoder = charset.newDecoder();
	CharsetEncoder encoder = charset.newEncoder();
	// Direct byte buffer for reading
	
	//TODO: make configurable 
	ByteBuffer downloadBuffer = ByteBuffer.allocateDirect(65536);
	ByteBuffer uploadBuffer = ByteBuffer.allocateDirect(8192);
	CharBuffer controlBuffer = CharBuffer.allocate(4096);
    SocketProvider socketProvider = null;
    
    public SuperFTPConnection()
    {
        
    }
    
    /**
     * @return Returns the password.
     */
    public String getPassword() {
        return password;
    }
    /**
     * @param password The password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return Returns the user.
     */
    public String getUser() {
        return user;
    }
    /**
     * @param user The user to set.
     */
    public void setUser(String user) throws ConfigurationException {
        if(user == null || user.length() == 0)
            throw new ConfigurationException("user must no be null or has a length of 0");
        this.user = user;
    }
   

    /**
     * @return Returns the address.
     */
    public InetSocketAddress getAddress() {
        return address;
    }
    /**
     * @param address The address to set.
     */
    public void setAddress(InetSocketAddress address)
    {
        this.address = address;
    }


    /**
     * @return Returns the account.
     */
    public String getAccount() {
        return account;
    }
    /**
     * @param account The account to set.
     */
    public void setAccount(String account) {
        this.account = account;
    }
    /* (non-Javadoc)
     * @see org.ftp4che.FTPConnection#setPassiveMode(boolean)
     */
    public void setPassiveMode(boolean mode) {
      this.passiveMode = mode;  
    }
    
    /* (non-Javadoc)
     * @see org.ftp4che.FTPConnection#setPassiveMode(boolean)
     */
    public boolean isPassiveMode() {
        return passiveMode;
    }
    
    public List getDirectoryListing()  throws IOException
    {
        //TODO: remove after implemting all other Connections
        return null;
    }
    
    public List getDirectoryListing(String directory)  throws IOException
    {
        //TODO: remove after implemting all other Connections
        
        return null;
    }
    
    public void setTimeout(long millis) {
        this.timeout = millis;
    }
    
    public long getTimeout()
    {
        return timeout;
    }
    
    public void sendPortCommand(InetAddress inetaddress, int localport) throws IOException
    {
        //TODO: remove after implemting all other Connections
        
    }
    
    
}
