/**
 * Created on 11.06.2005
 * @author arnold, kurt
 */
package org.ftp4che.util;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;


import org.apache.log4j.Logger;

public class SocketProvider {

	private int sslMode = 0; // 0 = no ssl ; 1 = implicit ssl ; 2 = auth tls ; 3 = auth ssl
    SocketChannel socketChan = null;
    SSLEngineResult res;
    SSLEngine sslEngine;
	Logger log = Logger.getLogger(SocketProvider.class.getName());
	
	ByteBuffer clientIn, clientOut, cTOs, sTOc, wbuf;
	
	public SocketProvider() throws IOException {
		socketChan = SocketChannel.open();
	}
    
    public SocketProvider( int sslMode ) throws IOException {
        socketChan = SocketChannel.open();
        setSSLMode( sslMode );
    }
	
	public SocketProvider( SocketChannel socketChan ) {
		this.socketChan = socketChan;
	}
    
    public SocketProvider( SocketChannel socketChan, int sslMode ) {
        this.socketChan = socketChan;
        setSSLMode( sslMode );
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
        if ( this.sslMode > 0 ) {
            sslEngine.closeOutbound();
            clientOut.clear();
            socketChan.write(wrap(clientOut));
            socketChan.close();
        } else        
            socketChan.close();
	}
	
	public SelectableChannel configureBlocking( boolean blockingState ) throws IOException {
		return socketChan.configureBlocking(blockingState);
	}
	

	public int write(ByteBuffer src) throws IOException {
        if ( this.sslMode > 0 )
            return socketChan.write(wrap(src));
		return socketChan.write(src);
	}
	
	public int read( ByteBuffer dst ) throws IOException {
		return socketChan.read(dst);
	}
	
	
	public String toString() {
		return socketChan.socket().getInetAddress().getHostAddress() + ":" + socketChan.socket().getPort();
	}

    /**
     * @return Returns the sslMode.
     */
    public int getSSLMode() {
        return sslMode;
    }

    /**
     * @param sslMode The sslMode to set.
     */
    public void setSSLMode(int sslMode) {
        this.sslMode = sslMode;
    }
    
    private void createBuffers(SSLSession session) {
        
        int appBufferMax = session.getApplicationBufferSize();
        int netBufferMax = session.getPacketBufferSize();
    
        clientIn = ByteBuffer.allocate(65536);
        clientOut = ByteBuffer.allocate(appBufferMax);
        wbuf = ByteBuffer.allocate(65536);
    
        cTOs = ByteBuffer.allocate(netBufferMax);
        sTOc = ByteBuffer.allocate(netBufferMax);
    
    }
    
    private synchronized ByteBuffer wrap(ByteBuffer b) throws SSLException {
        return null;
    }
    
    private synchronized ByteBuffer unwrap(ByteBuffer b) throws SSLException {
        return null;
    }
    
    public boolean isInboundDone() {
        return sslEngine.isInboundDone();
    }
}
