/**
 * Created on 11.06.2005
 * @author arnold, kurt
 */
package org.ftp4che.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;


import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import org.apache.log4j.Logger;
import org.ftp4che.FTPConnection;




public class SocketProvider {


	private int sslMode = FTPConnection.FTP_CONNECTION; 

    SocketChannel socketChan = null;
	Logger log = Logger.getLogger(SocketProvider.class.getName());
    
	ByteBuffer applicationIn, applicationOut,networkIn,networkOut;
    SSLEngine engine;
    SSLEngineResult result;
    boolean isControllConnection = true;
    
	public SocketProvider() throws IOException {
		socketChan = SocketChannel.open();
	}
    
    public SocketProvider(boolean isControllConnection) throws IOException {
        this();
        setControllConnection(isControllConnection);
    }
    
	public SocketProvider( SocketChannel socketChan ) {
		this.socketChan = socketChan;
	}
    
    public SocketProvider(SocketChannel socketChan, boolean isControllConnection )
    {
        this(socketChan);
        setControllConnection(isControllConnection);
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
    
    public boolean needsCrypt()
    {
        return   ((this.sslMode == FTPConnection.AUTH_SSL_FTP_CONNECTION || 
                this.sslMode == FTPConnection.AUTH_TLS_FTP_CONNECTION) && !isControllConnection()) ||
                this.sslMode != FTPConnection.FTP_CONNECTION && isControllConnection();
    }
	
	public void close() throws IOException {

        if (needsCrypt())
        {
            engine.closeOutbound();   
            applicationOut.clear();
            socketChan.write(wrap(applicationOut));
        }        
        socketChan.close();
	}
	
	public SelectableChannel configureBlocking( boolean blockingState ) throws IOException {
		return socketChan.configureBlocking(blockingState);
	}
	

	public int write(ByteBuffer src) throws IOException 
	{
        if (needsCrypt())
        {
        	return socketChan.write(wrap(src));
        }
		return socketChan.write(src);
	}
	
	public int read( ByteBuffer dst ) throws IOException {
        if (needsCrypt())
        {
            networkIn.clear();
            socketChan.read(networkIn);
            networkIn.flip();
            return unwrap(networkIn).capacity();
        }
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
    
    private void createBuffers(int applicationBufferSize,int networkBufferSize) {        
        applicationIn = ByteBuffer.allocate(applicationBufferSize);
        applicationOut = ByteBuffer.allocate(applicationBufferSize);
        networkIn = ByteBuffer.allocate(networkBufferSize);
        networkOut = ByteBuffer.allocate(networkBufferSize);
    }
        
    public boolean isInboundDone() {
        return engine.isInboundDone();
    }
    
    public void negotiate() throws NoSuchAlgorithmException,KeyStoreException,KeyManagementException,IOException,InterruptedException
    {
        SSLContext context = SSLContext.getInstance("TLS");
        TrustManager[] trustManagers = new TrustManager[]
        {
                new EasyX509TrustManager(null)
        };
        context.init(null, trustManagers , null);
        engine = context.createSSLEngine();
        engine.setUseClientMode(true);
        engine.setEnableSessionCreation(true);
        SSLSession session = engine.getSession();
        createBuffers(session.getApplicationBufferSize(),session.getPacketBufferSize());
        applicationOut.clear();
        socketChan.write(wrap(applicationOut));
        
        while (result.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.FINISHED) {
            if (result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_UNWRAP) {
                networkIn.clear();
                while (socketChan.read(networkIn) < 1)
                    Thread.sleep(20);
                networkIn.flip();
                unwrap(networkIn);
            } else if (result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_WRAP) {
                applicationOut.clear();
                socketChan.write(wrap(applicationOut));
                if (result.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.FINISHED) {
                    applicationOut.clear();
                    socketChan.write(wrap(applicationOut));
                }
            }
            else 
            {
                Thread.sleep(100);
            }
        }
    
        applicationIn.clear();
        applicationIn.flip();
    }
    
    private synchronized ByteBuffer wrap(ByteBuffer appOut) throws SSLException {
        networkOut.clear();
        result = engine.wrap(appOut, networkOut);
        networkOut.flip();
        return networkOut;
    }

    private synchronized ByteBuffer unwrap(ByteBuffer netIn) throws SSLException {
        applicationIn.clear();
    
        //TODO: unklar!!
        while (netIn.hasRemaining()) {
            result = engine.unwrap(netIn, applicationIn);
            if (result.getHandshakeStatus() == 
                SSLEngineResult.HandshakeStatus.NEED_TASK) {
                // Task
                Runnable task;
                while ((task=engine.getDelegatedTask()) != null)
                {
                    task.run();
                }
            } else if (result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.FINISHED) {
                return applicationIn;
            } else if (result.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                return applicationIn;
            }
        }
       return applicationIn;
    }

    /**
     * @return Returns the isControllConnection.
     */
    public boolean isControllConnection() {
        return isControllConnection;
    }

    /**
     * @param isControllConnection The isControllConnection to set.
     */
    public void setControllConnection(boolean isControllConnection) {
        this.isControllConnection = isControllConnection;
    }
}
