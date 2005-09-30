package org.ftp4che.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.apache.log4j.Logger;
import org.ftp4che.exception.ProxyConnectionException;

public class Socks5 implements Proxy {
    
	public static final Logger log = Logger.getLogger(Socks5.class.getName());
	
    private String host;
    private String user;
    private String pass;
    private int port;
    private int primaryConnectionPort;
    
    private Socket socket = null;

    public Socks5(String proxyHost, int proxyPort) {
    	this(proxyHost, proxyPort, null, null);
    }
    
    public Socks5(String proxyHost, int proxyPort, String proxyUser, String proxyPass) {
        setHost(proxyHost);
        setPort(proxyPort);
        setUser(proxyUser);
        setPass(proxyPass);
    }

    /**
     * public Socket connect(String host, int port) throws IOException
     *
     * establishing connection to the given host, over the socks4 server
     * 
     * @param host       the wanted hostname / ip for connection 
     * @param port       the connection port
     * 
     * @return Socket    the connection socket over proxy
     */
    public Socket connect(String host, int port) throws ProxyConnectionException {
        setPrimaryConnectionPort(port);
        
        InetSocketAddress isa = null;
        
        try {
            isa = new InetSocketAddress( InetAddress.getByName(host), port );
        }catch(IOException ioe) {
            throw new ProxyConnectionException(-2, "SOCK4 - IOException: " + ioe.getMessage());
        }
        
        InetAddress addr = isa.getAddress();
        byte[] hostbytes = addr.getAddress();
        byte[] requestPacket = new byte[300];
        byte[] response = new byte[2];
        byte methodCount = 2;
        
        requestPacket[0] = 5; // means socks5 (field VN)
        requestPacket[1] = methodCount; // methods count (field NMETHODS)
        requestPacket[2] = 0; // X'00' NO AUTHENTICATION REQUIRED
//        requestPacket[3] = 1; // X'01' GSSAPI
        if (getUser() != null && getPass() != null)
        	requestPacket[3] = 2; // X'02' USERNAME/PASSWORD

        try {
            connectToProxy();
            
            this.socket.getOutputStream().write(requestPacket, 0, methodCount + 2);
            this.socket.getInputStream().read(response, 0, 2);
        }catch(IOException ioe) {
            throw new ProxyConnectionException(-2, "SOCK4 - IOException: " + ioe.getMessage());
        }
        
        // negotiation for connect method completed
        log.debug("Using method: " + response[1]);
        
        if (response[1] == 0) { // NO AUTHENTICATION REQUIRED
        	return request(hostbytes);
//        }else if (response[1] == 1) { // GSSAPI
        	
        }else if (response[1] == 2) { // USERNAME/PASSWORD
        	if(authUserPass(hostbytes))
        		return request(hostbytes);
        }        

        return null;
    }
    
    private Socket request(byte[] hostbytes) throws ProxyConnectionException {
    	byte[] requestPacket = new byte[1024];
    	byte[] response = new byte[10];
    	
    	requestPacket[0] = 5; // means socks5 (field VN)
    	requestPacket[1] = 1; // connect (field CMD)
    	requestPacket[2] = 0; // reserved (field RSV)
    	requestPacket[3] = 1; // IPv4 (field ATYP)
    	
        // adding the host adress bytes to the packet (field DST.ADDR)
        System.arraycopy(hostbytes, 0, requestPacket, 4, 4);
        
        requestPacket[8] = (byte) ( getPrimaryConnectionPort() >> 8 ); // (field DST.PORT)
        requestPacket[9] = (byte) ( getPrimaryConnectionPort() & 0x00ff ); // (field DST.PORT)
        
        try {
            this.socket.getOutputStream().write(requestPacket, 0, 10);
            this.socket.getInputStream().read(response, 0, 10);
        }catch(IOException ioe) {
            throw new ProxyConnectionException(-2, "SOCK5 - IOException: " + ioe.getMessage());
        }
        
        ProxyConnectionException pce = null;
        switch (response[1]) {
            case 0:
                break; // request successfull
            case 1:
                pce = new ProxyConnectionException(1, "SOCKS5 general SOCKS server failure");
                break;
            case 2:
                pce = new ProxyConnectionException(2, "SOCKS5 connection not allowed by ruleset");
                break;
            case 3:
                pce = new ProxyConnectionException(3, "SOCKS5 Network unreachable");
                break;
            case 4:
                pce = new ProxyConnectionException(4, "SOCKS5 Host unreachable");
                break;
            case 5:
                pce = new ProxyConnectionException(5, "SOCKS5 Connection refused");
                break;
            case 6:
                pce = new ProxyConnectionException(6, "SOCKS5 TTL expired");
                break;
            case 7:
                pce = new ProxyConnectionException(7, "SOCKS5 Command not supported");
                break;
            case 8:
                pce = new ProxyConnectionException(8, "SOCKS5 Address type not supported");
                break;
            case 9:
                pce = new ProxyConnectionException(9, "SOCKS5 to X'FF' unassigned");
                break;
            default:
                pce = new ProxyConnectionException(-1, "SOCKS5 unknown proxy response");
                break;
        }
        
        return this.socket;
    }
    
    private boolean authUserPass(byte[] hostbytes) throws ProxyConnectionException {
    	byte[] requestPacket = new byte[1024];
    	byte[] response = new byte[2];
    	byte userLen = (byte) getUser().getBytes().length;
    	byte passLen = (byte) getPass().getBytes().length;
    	
    	requestPacket[0] = 1;
    	requestPacket[1] = userLen;
    	// adding the username to the packet
        System.arraycopy(getUser().getBytes(), 0, requestPacket, 2, userLen);
        requestPacket[2 + userLen] = passLen;
    	// adding the password to the packet
        System.arraycopy(getPass().getBytes(), 0, requestPacket, 3 + userLen, passLen);
        
        try {
            this.socket.getOutputStream().write(requestPacket, 0, 3 + userLen + passLen);
            this.socket.getInputStream().read(response, 0, 2);
        }catch(IOException ioe) {
            throw new ProxyConnectionException(-2, "SOCK5 - IOException: " + ioe.getMessage());
        }
        
        ProxyConnectionException pce = null;
        switch (response[1]) {
            case 0:
            	return true; // auth successfull
            case 1:
                pce = new ProxyConnectionException(1, "SOCKS5 general SOCKS server failure");
                break;
            case 2:
                pce = new ProxyConnectionException(2, "SOCKS5 connection not allowed by ruleset");
                break;
            case 3:
                pce = new ProxyConnectionException(3, "SOCKS5 Network unreachable");
                break;
            case 4:
                pce = new ProxyConnectionException(4, "SOCKS5 Host unreachable");
                break;
            case 5:
                pce = new ProxyConnectionException(5, "SOCKS5 Connection refused");
                break;
            case 6:
                pce = new ProxyConnectionException(6, "SOCKS5 TTL expired");
                break;
            case 7:
                pce = new ProxyConnectionException(7, "SOCKS5 Command not supported");
                break;
            case 8:
                pce = new ProxyConnectionException(8, "SOCKS5 Address type not supported");
                break;
            case 9:
                pce = new ProxyConnectionException(9, "SOCKS5 to X'FF' unassigned");
                break;
            default:
                pce = new ProxyConnectionException(-1, "SOCKS5 unknown proxy response");
                break;
        }
        
        return false;
    }
    
    private void connectToProxy() throws IOException {
        this.socket = new Socket();
        this.socket.setSoTimeout(Integer.MAX_VALUE);
        this.socket.connect((SocketAddress) new InetSocketAddress( InetAddress.getByName(getHost()), getPort()), 20000);
    } 
    
    public Socket bind(InetSocketAddress isa) throws IOException {
    	return null;
    }


    /**
     * @return Returns the host.
     */
    public String getHost() {
        return host;
    }


    /**
     * @param host The host to set.
     */
    public void setHost(String host) {
        this.host = host;
    }


    /**
     * @return Returns the port.
     */
    public int getPort() {
        return port;
    }


    /**
     * @param port The port to set.
     */
    public void setPort(int port) {
        this.port = port;
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
    public void setUser(String user) {
        this.user = user;
    }
    
    /**
     * @return Returns the user.
     */
    public String getPass() {
        return pass;
    }


    /**
     * @param user The user to set.
     */
    public void setPass(String pass) {
        this.pass = pass;
    }

    /**
     * @return Returns the primaryConnectionPort.
     */
    public int getPrimaryConnectionPort() {
        return primaryConnectionPort;
    }

    /**
     * @param primaryConnectionPort The primaryConnectionPort to set.
     */
    public void setPrimaryConnectionPort(int primaryConnectionPort) {
        this.primaryConnectionPort = primaryConnectionPort;
    }
    
    public static void main (String args[]) throws Exception {
        Socks5 proxy = new Socks5("127.0.0.1",1080, "anonymous","test");
        Socket socket = null;
        
        try {
            socket = proxy.connect("172.25.12.195", 23);
        }catch(Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("looks like we are connected to the proxy ...");

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        while ( (line = in.readLine()) != null) {
            System.out.println(line);
        }
    }
}
