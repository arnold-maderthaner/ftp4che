/**                                                                         *
*  This file is part of ftp4che.                                            *
*                                                                           *
*  This library is free software; you can redistribute it and/or modify it  *
*  under the terms of the GNU General Public License as published    		*
*  by the Free Software Foundation; either version 2 of the License, or     *
*  (at your option) any later version.                                      *
*                                                                           *
*  This library is distributed in the hope that it will be useful, but      *
*  WITHOUT ANY WARRANTY; without even the implied warranty of               *
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU        *
*  General Public License for more details.                          		*
*                                                                           *
*  You should have received a copy of the GNU General Public		        *
*  License along with this library; if not, write to the Free Software      *
*  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA  *
*                                                                           *
*****************************************************************************/
package org.ftp4che.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.log4j.Logger;
import org.ftp4che.FTPConnection;


public class SSLSupport {
	//TODO: Use this class to integrate ssl support to ftp4che14
	private SocketChannel channel;
	private SSLSocket sslSocket = null;
	private int mode;
	private Logger log = Logger.getLogger(SSLSupport.class.getName());
	private ByteBuffer application,network;
	private SSLContext context;
    private boolean initialHandshake = false;
 
    
	public SSLSupport(SocketChannel channel, int mode)
	{
		setMode(mode);
		setChannel(channel);
	}
	
	public void initEngineAndBuffers() throws NoSuchAlgorithmException,KeyStoreException,KeyManagementException,SSLException,IOException
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
	    SSLSocketFactory sslFact = (SSLSocketFactory)SSLSocketFactory.getDefault();
	    sslSocket = (SSLSocket)sslFact.createSocket(channel.socket(),channel.socket().getInetAddress().getHostAddress(),channel.socket().getPort(),true);
	    sslSocket.setEnableSessionCreation(false);
	   
	    sslSocket.setUseClientMode(true);
	    SSLSession session = sslSocket.getSession();
		application = ByteBuffer.allocate(32000);
	    network = ByteBuffer.allocate(32000);
		
	}
	
	public void handshake() throws SSLException,IOException
	{
		log.debug("Starting handshake");		
		initialHandshake = true;
		sslSocket.startHandshake();
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
    	return channel.write(src);
    }
    
    public int read(ByteBuffer dst) throws IOException {     
        if (initialHandshake) {
            return 0;
        }
        while(channel.read(dst) > 0)
        ;
        dst.flip();
        return dst.remaining();
    }
    
}
