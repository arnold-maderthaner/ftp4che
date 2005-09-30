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
import org.ftp4che.io.ReplyWorker;

public class Socks4 implements Proxy {
    
	public static final Logger log = Logger.getLogger(Socks4.class.getName());
	
    private String host;
    private String user;
    private int port;
    private int primaryConnectionPort;
    
    private Socket socket = null;

    public Socks4(String proxyHost, int proxyPort, String proxyUser) {
        setHost(proxyHost);
        setPort(proxyPort);
        setUser(proxyUser);
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
        
        requestPacket[0] = 4; // means socks4 (field VN)
        requestPacket[1] = 1; // means connect (field CD)
        requestPacket[2] = new Integer((port & 0xff00) >> 8).byteValue();//(byte) ( (port & 0xff00) >> 8); // (field DSTPORT)
        requestPacket[3] = new Integer((port & 0x00ff) ).byteValue(); //(byte) ( port & 0x00ff ); // (field DSTPORT)
        
        // adding the host adress bytes to the packet (field DSTIP)
        System.arraycopy(hostbytes, 0, requestPacket, 4, 4);
        
        // adding user id to packet (field USERID)
        System.arraycopy(getUser().getBytes(), 0, requestPacket, 8, getUser().length());
        
        // terminate the packet
        requestPacket[9 + getUser().length()] = 0;

        byte[] response = new byte[8];
        
        // connect the socket
        try {
            connectToProxy();
            
            this.socket.getOutputStream().write(requestPacket, 0, 9 + getUser().length());
            this.socket.getInputStream().read(response, 0, 8);
        }catch(IOException ioe) {
            throw new ProxyConnectionException(-2, "SOCK4 - IOException: " + ioe.getMessage());
        }
        
        ProxyConnectionException pce = null;
        switch (response[1]) {
            case 90:
                break; // connect successfull
            case 91:
                pce = new ProxyConnectionException(91, "SOCKS4 request rejected or failed");
                break;
            case 92:
                pce = new ProxyConnectionException(92, "SOCKS4 request rejected becasue SOCKS server cannot connect to identd on the client");
                break;
            case 93:
                pce = new ProxyConnectionException(93, "SOCKS4 request rejected because the client program and identd report different user-ids.");
                break;
            default:
                pce = new ProxyConnectionException(-1, "SOCKS4 unknown proxy response");
                break;
        }
        
        if (pce != null) {
            try {
                this.socket.close();
            }catch(IOException ioe) {}
            throw pce;
        }
        
        return socket;
    }
    
    private void connectToProxy() throws IOException {
        this.socket = new Socket();
        this.socket.setSoTimeout(Integer.MAX_VALUE);
        this.socket.connect((SocketAddress) new InetSocketAddress( InetAddress.getByName(getHost()), getPort()), 20000);
    } 
    
    public Socket bind(InetSocketAddress isa) throws IOException {
        
        InetAddress addr = isa.getAddress();
        byte[] hostbytes = addr.getAddress();
        byte[] requestPacket = new byte[300];
        
        requestPacket[0] = 4; // means socks4 (field VN)
        requestPacket[1] = 2; // means bind (field CD)
        requestPacket[2] = (byte) ( getPrimaryConnectionPort() >> 8 ); // (field DSTPORT)
        requestPacket[3] = (byte) ( getPrimaryConnectionPort() & 0x00ff ); // (field DSTPORT)
        
        // adding the host adress bytes to the packet (field DSTIP)
        System.arraycopy(hostbytes, 0, requestPacket, 4, 4);
        
        // adding user id to packet (field USERID)
        System.arraycopy(getUser().getBytes(), 0, requestPacket, 8, getUser().length());
        
        // terminate the packet
        requestPacket[9 + getUser().length()] = 0;
        
        byte[] response = new byte[8];
        // connect the socket
        try {
            connectToProxy();
            
            this.socket.getOutputStream().write(requestPacket, 0, 9 + getUser().length());
            this.socket.getInputStream().read(response, 0, 8);
        }catch(IOException ioe) {
            throw new ProxyConnectionException(-2, "SOCK4 - IOException: " + ioe.getMessage());
        }
        
        ProxyConnectionException pce = null;
        switch (response[1]) {
            case 90:
                break; // bind successfull
            case 91:
                pce = new ProxyConnectionException(91, "SOCKS4 request rejected or failed");
                break;
            case 92:
                pce = new ProxyConnectionException(92, "SOCKS4 request rejected becasue SOCKS server cannot connect to identd on the client");
                break;
            case 93:
                pce = new ProxyConnectionException(93, "SOCKS4 request rejected because the client program and identd report different user-ids.");
                break;
            default:
                pce = new ProxyConnectionException(-1, "SOCKS4 unknown proxy response");
                break;
        }
     
        int bindPort = ((int) response[2]) & ((int) response[3]);
        byte[] bindAddr = { response[4] ,response[5], response[6], response[7] };
        InetAddress inetAddress = InetAddress.getByAddress(bindAddr);
        
        log.debug("Binding to: " + inetAddress.getHostAddress() + ":" + bindPort);
        
        return this.socket;
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
        Socks4 proxy = new Socks4("211.250.81.252",1080, "anonymous");
        Socket socket = null;
        
        try {
            socket = proxy.connect("195.58.170.125", 21);
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
