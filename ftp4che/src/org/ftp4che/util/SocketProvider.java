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

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import org.apache.log4j.Logger;
import org.ftp4che.FTPConnection;

public class SocketProvider {

	private int sslType;
	SocketChannel socketChan = null;
	SSLEngine sslEngine;
	SSLEngineResult res;
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
	
	public boolean isInboundDone() {
		return sslEngine.isInboundDone();
	}
	
	public boolean isConnected() {
		return socketChan.isConnected();
	}
	
	public void close() throws IOException {
		if (sslType == FTPConnection.AUTH_TLS_FTP_CONNECTION ||
			sslType == FTPConnection.AUTH_SSL_FTP_CONNECTION) {
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
	
	private synchronized ByteBuffer wrap( ByteBuffer b ) throws SSLException {
		cTOs.clear();
		res = sslEngine.wrap(b, cTOs);
		cTOs.flip();
		log.debug("wrap: " + res.toString());
		return cTOs;
	}
	
	private synchronized ByteBuffer unwrap( ByteBuffer b ) throws SSLException {
		clientIn.clear();
		int pos;

		log.debug("bytes remaining: " + b.remaining());
		while (b.hasRemaining()) {
			log.debug("bytes remaining: " + b.remaining());
			res = sslEngine.unwrap(b, clientIn);
			log.debug("unwrap: " + res.toString());
			if (res.getHandshakeStatus() == 
				SSLEngineResult.HandshakeStatus.NEED_TASK) {
				// Task
				Runnable task;
				while ((task=sslEngine.getDelegatedTask()) != null)
				{
					log.debug("task ...");
					task.run();
				}
				log.debug("task: " + res.toString());
			} else if (res.getHandshakeStatus() == 
				SSLEngineResult.HandshakeStatus.FINISHED) {
				return clientIn;
			} else if (res.getStatus() == 
				SSLEngineResult.Status.BUFFER_UNDERFLOW) {
				log.error("buffer underflow occured");
				log.error("bytes remaining: " + b.remaining());
				return clientIn;
			}
		}
		return clientIn;
	}

	private void createBuffers( SSLSession session ) {
	
		int appBufferMax = session.getApplicationBufferSize();
		int netBufferMax = session.getPacketBufferSize();
	
		clientIn = ByteBuffer.allocate(65536);
		clientOut = ByteBuffer.allocate(appBufferMax);
		wbuf = ByteBuffer.allocate(65536);
	
		cTOs = ByteBuffer.allocate(netBufferMax);
		sTOc = ByteBuffer.allocate(netBufferMax);
	
	}

	public int write(ByteBuffer src) throws IOException {
		if (sslType == FTPConnection.AUTH_TLS_FTP_CONNECTION ||
			sslType == FTPConnection.AUTH_SSL_FTP_CONNECTION) {
			return socketChan.write(wrap(src));
		}
		return socketChan.write(src);
	}
	
	public int read( ByteBuffer dst ) throws IOException {
		log.debug("read ...");
		int amount = 0, limit;
		if (sslType == 4) {
			// test if there was a buffer overflow in dst
			if (clientIn.hasRemaining()) {
				limit = Math.min(clientIn.remaining(), dst.remaining());
				for (int i = 0; i < limit; i++) {
					dst.put(clientIn.get());
					amount++;
				}
				return amount;
			}
			// test if some bytes left from last read (e.g. BUFFER_UNDERFLOW)
			if (sTOc.hasRemaining()) {
				unwrap(sTOc);
				clientIn.flip();
				limit = Math.min(clientIn.limit(), dst.remaining());
				for (int i = 0; i < limit; i++) {
					dst.put(clientIn.get());
					amount++;
				}
				if (res.getStatus() != SSLEngineResult.Status.BUFFER_UNDERFLOW) {
					sTOc.clear();
					sTOc.flip();
					return amount;
				}
			}
			if (!sTOc.hasRemaining())
				sTOc.clear();
			else
				sTOc.compact();
			
			if (socketChan.read(sTOc) == -1) {
				log.error("closing SocketProvider");
				sTOc.clear();
				sTOc.flip();
				return -1;
			}
			sTOc.flip();
			unwrap(sTOc);
			// write in dst
			clientIn.flip();
			limit = Math.min(clientIn.limit(), dst.remaining());
			for (int i = 0; i < limit; i++) {
				dst.put(clientIn.get());
				amount++;
			}
			log.debug("bytes remaining: " + dst.remaining());
			return amount;
		}
		log.debug("Last Line before i read from server");

		return socketChan.read(dst);
	}
	
	public int tryTLS( int sslType ) throws IOException {
		this.sslType = sslType;
		if (this.sslType == 0)
			return 0;

		SSLContext sslContext = null;
		try {
			// create SSLContext
			sslContext = SSLContext.getInstance("TLS");
			
			sslContext.init(null, new TrustManager[] {new EasyX509TrustManager(null)}, null);
			// create Engine
			sslEngine = sslContext.createSSLEngine();
			// begin
			sslEngine.setUseClientMode(true);
			
			sslEngine.setEnableSessionCreation(true);
			SSLSession session = sslEngine.getSession();
			createBuffers(session);
			// wrap
			clientOut.clear();
			socketChan.write(wrap(clientOut));
			while (res.getHandshakeStatus() != 
					SSLEngineResult.HandshakeStatus.FINISHED) {
				if (res.getHandshakeStatus() == 
					SSLEngineResult.HandshakeStatus.NEED_UNWRAP) {
					// unwrap
					sTOc.clear();
					while (socketChan.read(sTOc) < 1)
						Thread.sleep(20);
					sTOc.flip();
					unwrap(sTOc);
					if (res.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.FINISHED) {
						clientOut.clear();
						socketChan.write(wrap(clientOut));
					}
				} else if (res.getHandshakeStatus() == 
					SSLEngineResult.HandshakeStatus.NEED_WRAP) {
					// wrap
					clientOut.clear();
					socketChan.write(wrap(clientOut));
				} else {Thread.sleep(1000);}
			}
			clientIn.clear();
			clientIn.flip();
			this.sslType = 4;
			log.debug("ssl connection established");
		} catch (Exception e) {
			e.printStackTrace(System.out);
			this.sslType = 0;
		}
		return this.sslType;
	}
	
	public String toString() {
		return socketChan.socket().getInetAddress().getHostAddress() + ":" + socketChan.socket().getPort();
	}
}
