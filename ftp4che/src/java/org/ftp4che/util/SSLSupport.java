package org.ftp4che.util;

import java.io.IOException;
import java.nio.ByteBuffer;
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


public class SSLSupport {
	private SocketChannel channel;
	private int mode;
	private Logger log = Logger.getLogger(SSLSupport.class.getName());
	private SSLEngineResult.HandshakeStatus handshakeStatus;
	private ByteBuffer application,network;
	private SSLEngine engine;
	private SSLContext context;
    private boolean initialHandshake = false;
    private SSLEngineResult.Status status = null;
    
	public SSLSupport(SocketChannel channel, int mode)
	{
		setMode(mode);
		setChannel(channel);
	}
	
	public void initEngineAndBuffers() throws NoSuchAlgorithmException,KeyStoreException,KeyManagementException,SSLException
	{
		if(mode == FTPConnection.AUTH_SSL_FTP_CONNECTION)
		  context = SSLContext.getInstance("SSL");
		else
		  context = SSLContext.getInstance("TLS");
	    TrustManager[] trustManagers = new TrustManager[]
	    {
	        new EasyX509TrustManager(null)
	    };
	    context.init(null, trustManagers , null);
	    engine = context.createSSLEngine();
	    engine.setUseClientMode(true);
	    engine.setEnableSessionCreation(true);
	    SSLSession session = engine.getSession();
		application = ByteBuffer.allocate(session.getApplicationBufferSize());
	    network = ByteBuffer.allocate(session.getPacketBufferSize());
		log.debug("Starting handshake");		
		engine.beginHandshake();
		handshakeStatus = engine.getHandshakeStatus();
		initialHandshake = true;
	}
	
	private void openTask() {
            Runnable task;
            while ((task=engine.getDelegatedTask()) != null)
            {
                task.run();
            }
		handshakeStatus = engine.getHandshakeStatus();
	}
	
	public void handshake() throws SSLException,IOException
	{
		while (true) {
			SSLEngineResult result = null;
			log.debug("Handshake status:" + handshakeStatus.toString());
			switch (handshakeStatus) {
			case NEED_WRAP:
					network.clear();
					result = engine.wrap(application, network);
					log.info("Wrap:" + result);
					handshakeStatus = result.getHandshakeStatus();
					network.flip();
					if (!sendData())
						return;
					break;
			case FINISHED:
					initialHandshake = false;
				return;
			case NEED_UNWRAP:
                network.clear();
				unwrapData();	
			break;
			case NEED_TASK:
				openTask();
				break;
			default:
				log.debug("You should never reach this status:" + result);
				return;
			}
		}
		
	}
	
	private int unwrapData() throws IOException {
        application.clear();
        
        int bytesRead = 0;
        
        while ((bytesRead = channel.read(network)) < 1) {
            try {
                Thread.sleep(20);
            }catch (Exception e) { e.printStackTrace(); }
        }
     
		log.debug("Read from socket: " + bytesRead);			
		if (bytesRead == -1) {
			engine.closeInbound();			
			if (network.position() == 0 ||
					status == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
				return -1;
			}
		}
		
		network.flip();
		SSLEngineResult res = null;
        
        while (network.hasRemaining()) {
            log.debug("remaining network: " + network.remaining());
            log.debug("remaining application: " + application.remaining());
            res = engine.unwrap(network, application);
            log.debug(res);
            if (res.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                openTask();
            } else if (res.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.FINISHED) {
                initialHandshake = false;
                log.debug("Handshake finished");
            } else if (res.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                log.debug("underflow");
                log.debug("remaining: "+network.remaining());
            } else if (res.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW)
            {
                log.debug("overflow");
                log.debug("remaining network: " + network.remaining());
                log.debug("remaining application: " + application.remaining());
                break;
            }
        }
  	
		if (application.position() == 0 && 
				res.getStatus() == SSLEngineResult.Status.OK &&
				network.hasRemaining()) {
			res = engine.unwrap(network, application);
			log.info("Unwrapping:\n" + res);			
		}
		status = res.getStatus();
		handshakeStatus = res.getHandshakeStatus();

		if (status == SSLEngineResult.Status.CLOSED) {
			try
			{
				log.debug("Connection is being closed by peer.");
				network.clear();
				application.clear();
				res = engine.wrap(application, network);
			} catch (SSLException e1) {
				log.warn("Error during shutdown.\n" + e1.toString());
				try {
					channel.close();
				} catch (IOException e) {	
					//DO NOTHING 
				}
			}
			network.flip();
			sendData();
			return -1;
		}	
		
		network.compact();
		application.flip();
		
		if (handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_TASK ||
				handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_WRAP ||
				handshakeStatus == SSLEngineResult.HandshakeStatus.FINISHED) 
		{
			log.debug("Redo the handshake()");
			handshake();
		}
		
		return application.remaining();
	}
	
	private boolean sendData() throws IOException {		
		int written;
		try {
			written = channel.write(network);
		} catch (IOException ioe) {
			network.position(network.limit());
			throw ioe;
		}
		log.debug("Written to socket: " + written);	
		if (network.hasRemaining()) {
			return false;
		}  else {
			return true;
		}
	}

	public SocketChannel getChannel() {
		return channel;
	}

	public void setChannel(SocketChannel channel) {
		this.channel = channel;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

    public int write(ByteBuffer src) throws IOException {
        if (initialHandshake) {
            log.debug("Don't call write till handshake is done");
            return 0;
        }
        log.debug("Trying to write");
        
//        if (network.hasRemaining()) {
//            return 0;
//        }

        network.clear();
        SSLEngineResult res = engine.wrap(src, network);
        
        log.info("Wrap: " + res);

        network.flip();
        sendData();
        network.clear();
        return res.bytesConsumed();
    }
    
    public int read(ByteBuffer dst) throws IOException {     
        if (initialHandshake) {
            return 0;
        }

        if (engine.isInboundDone()) {
            return -1;
        }

        if (!application.hasRemaining()) {
            int byteCount = unwrapData(); 
            
            if (byteCount <= 0) {
                return byteCount;
            } 
        }
        int limit = Math.min(application.remaining(), dst.remaining());
        for (int i = 0; i < limit; i++) {
                dst.put(application.get());
        }

        return limit;
    }
}
