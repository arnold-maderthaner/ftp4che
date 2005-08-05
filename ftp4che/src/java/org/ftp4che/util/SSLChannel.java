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


public class SSLChannel {
	private SocketChannel channel;
	private int mode;
	private Logger log = Logger.getLogger(SSLChannel.class.getName());
	private SSLEngineResult.HandshakeStatus handshakeStatus;
	private ByteBuffer applicationIn, applicationOut,networkIn,networkOut;
	private SSLEngine engine;
	private SSLContext context;
    private boolean initialHandshake = false;
    private SSLEngineResult.Status status = null;
    
	public SSLChannel(SocketChannel channel, int mode)
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
		applicationIn = ByteBuffer.allocate(session.getApplicationBufferSize());
	    applicationOut = ByteBuffer.allocate(session.getApplicationBufferSize());
	    networkIn = ByteBuffer.allocate(session.getPacketBufferSize());
	    networkOut = ByteBuffer.allocate(session.getPacketBufferSize());
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
					networkOut.clear();
					result = engine.wrap(applicationOut, networkOut);
					log.info("Wrap:" + result);
					handshakeStatus = result.getHandshakeStatus();
					networkOut.flip();
					if (!sendData())
						return;
					break;
			case FINISHED:
					initialHandshake = false;
				return;
			case NEED_UNWRAP:
                networkIn.clear();
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
        applicationIn.clear();
        
        int bytesRead = 0;
        
        while ((bytesRead = channel.read(networkIn)) < 1) {
            try {
                Thread.sleep(20);
            }catch (Exception e) { e.printStackTrace(); }
        }
     
		log.debug("Read from socket: " + bytesRead);			
		if (bytesRead == -1) {
			engine.closeInbound();			
			if (networkIn.position() == 0 ||
					status == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
				return -1;
			}
		}
		
		networkIn.flip();
		SSLEngineResult res = null;
        
        while (networkIn.hasRemaining()) {
            log.debug("remaining: " + networkIn.remaining());
            res = engine.unwrap(networkIn, applicationIn);
            log.debug(res.getHandshakeStatus());
            if (res.getHandshakeStatus() == 
                SSLEngineResult.HandshakeStatus.NEED_TASK) {
                    openTask();
                }
         else if (res.getHandshakeStatus() == 
            SSLEngineResult.HandshakeStatus.FINISHED) {
             log.debug("Handshake finished");
        } else if (res.getStatus() == 
            SSLEngineResult.Status.BUFFER_UNDERFLOW) {
            log.debug("underflow");
            log.debug("remaining: "+networkIn.remaining());
        }
        }
   
		if (res.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.FINISHED) {
			initialHandshake = false;
		}
		
		if (applicationIn.position() == 0 && 
				res.getStatus() == SSLEngineResult.Status.OK &&
				networkIn.hasRemaining()) {
			res = engine.unwrap(networkIn, applicationIn);
			log.info("Unwrapping:\n" + res);			
		}
		status = res.getStatus();
		handshakeStatus = res.getHandshakeStatus();

		if (status == SSLEngineResult.Status.CLOSED) {
			try
			{
				log.debug("Connection is being closed by peer.");
				networkOut.clear();
				applicationOut.clear();
				res = engine.wrap(applicationOut, networkOut);
			} catch (SSLException e1) {
				log.warn("Error during shutdown.\n" + e1.toString());
				try {
					channel.close();
				} catch (IOException e) {	
					//DO NOTHING 
				}
			}
			networkOut.flip();
			sendData();
			return -1;
		}	
		
		networkOut.compact();
		applicationOut.flip();
		
		if (handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_TASK ||
				handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_WRAP ||
				handshakeStatus == SSLEngineResult.HandshakeStatus.FINISHED) 
		{
			log.debug("Redo the handshake()");
			handshake();
		}
		
		return applicationOut.remaining();
	}
	
	private boolean sendData() throws IOException {		
		int written;
		try {
			written = channel.write(networkOut);
		} catch (IOException ioe) {
			networkOut.position(networkOut.limit());
			throw ioe;
		}
		log.debug("Written to socket: " + written);	
		if (networkOut.hasRemaining()) {
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

    public int write() throws IOException {
        if (initialHandshake) {
            log.debug("Don't call write till handshake is done");
            return 0;
        }
        log.debug("Trying to write");
        
//        if (networkOut.hasRemaining()) {
//            return 0;
//        }

        networkOut.clear();
        SSLEngineResult res = engine.wrap(applicationOut, networkOut);
        
        log.info("Wrap: " + res);

        networkOut.flip();
        sendData();

        return res.bytesConsumed();
    }

    /**
     * @return Returns the applicationIn.
     */
    public ByteBuffer getApplicationIn() {
        return applicationIn;
    }

    /**
     * @param applicationIn The applicationIn to set.
     */
    public void setApplicationIn(ByteBuffer applicationIn) {
        this.applicationIn = applicationIn;
    }

    /**
     * @return Returns the applicationOut.
     */
    public ByteBuffer getApplicationOut() {
        return applicationOut;
    }

    /**
     * @param applicationOut The applicationOut to set.
     */
    public void setApplicationOut(ByteBuffer applicationOut) {
        this.applicationOut = applicationOut;
    }
    
    public int read() throws IOException {     
        if (initialHandshake) {
            return 0;
        }

        if (engine.isInboundDone()) {
            return -1;
        }

        if (applicationIn.hasRemaining()) {
            int byteCount = unwrapData(); 
            
            if (byteCount <= 0) {
                return byteCount;
            } 
        }
        
        return applicationIn.remaining();
    }

	public ByteBuffer getNetworkIn() {
		return networkIn;
	}

	public void setNetworkIn(ByteBuffer networkIn) {
		this.networkIn = networkIn;
	}

	public ByteBuffer getNetworkOut() {
		return networkOut;
	}

	public void setNetworkOut(ByteBuffer networkOut) {
		this.networkOut = networkOut;
	}
}
