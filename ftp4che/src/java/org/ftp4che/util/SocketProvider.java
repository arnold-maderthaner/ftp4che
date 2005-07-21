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

	private boolean sslMode = false; // default: ssl = off
    SocketChannel socketChan = null;
    SSLEngineResult res;
    SSLEngine sslEngine;
	Logger log = Logger.getLogger(SocketProvider.class.getName());
	
	ByteBuffer clientIn, clientOut, cTOs, sTOc, wbuf;
	
	public SocketProvider() throws IOException {
		socketChan = SocketChannel.open();
	}
    
	public SocketProvider( SocketChannel socketChan ) {
		this.socketChan = socketChan;
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
