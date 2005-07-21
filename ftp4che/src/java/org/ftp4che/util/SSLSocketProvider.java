package org.ftp4che.util;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.log4j.Logger;


public class SSLSocketProvider {

    // default logger
    Logger log = Logger.getLogger(SSLSocketProvider.class.getName());
    SocketChannel socketChan = null;
    
    public SSLSocketProvider() {
        super();
        
        try {
            SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) socketFactory.createSocket();
            socketChan = socket.getChannel();
        }catch(IOException ioe) {
            log.error("Could not initialize a SSL socket!", ioe);
        }
    }
    
    public SSLSocketProvider( SSLSocket socket ) {
        socketChan = socket.getChannel();       
    }

    public boolean connect( SocketAddress remote ) throws IOException {
        return socketChan.connect(remote);
    }
    
    public boolean finishConnect() throws IOException {
        return socketChan.finishConnect();
    }
    
    public Socket socket() {
        return socketChan.socket();
    }
    
    public boolean isConnected() {
        return socketChan.isConnected();
    }
    
    public void close() throws IOException {
        socketChan.close();
    }
    
    public SelectableChannel configureBlocking( boolean blockingState ) throws IOException {
        return socketChan.configureBlocking(blockingState);
    }
    
    public int write(ByteBuffer src) throws IOException {
        return socketChan.write(src);
    }
    
    public int read( ByteBuffer dst ) throws IOException {
        return socketChan.read(dst);
    }
    
    public String toString() {
        return socketChan.socket().getInetAddress().getHostAddress() + ":" + socketChan.socket().getPort();
    }
}
