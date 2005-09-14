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
package org.ftp4che.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.log4j.Logger;
import org.ftp4che.FTPConnection;
import org.ftp4che.util.EasyX509TrustManager;


public class SSLSupport {
	private Socket socket;
	private SSLSocket sslSocket = null;
	private int mode;
	private static final Logger log = Logger.getLogger(SSLSupport.class.getName());	
	private SSLContext context;
    private OutputStream out = null;
    private InputStream in = null;
    private byte[] readArray = new byte[16384];
    private boolean controllConnection;
    
	public SSLSupport(Socket socket, int mode,boolean controllConnection)
	{
		setMode(mode);
		setSocket(socket);
        setControllConnection(controllConnection);
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
	    SSLSocketFactory sslFact = context.getSocketFactory();
	    sslSocket = (SSLSocket)sslFact.createSocket(socket,socket.getInetAddress().getHostAddress(),socket.getPort(),true);
        out = sslSocket.getOutputStream();
        in = sslSocket.getInputStream();
	    sslSocket.setEnableSessionCreation(true);
	    sslSocket.setUseClientMode(true);
	}
	
	public void handshake() throws SSLException,IOException
	{
		log.debug("Starting handshake");		
		sslSocket.startHandshake();
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

    public int write(ByteBuffer src) throws IOException {
    	int byteCount = src.remaining();
        out.write(src.array(),0,byteCount);
  		return byteCount;
    }
    
    public int read(ByteBuffer dst) throws IOException {     
        int byteCount = 0;
        if(isControllConnection())
            byteCount = in.read(readArray,0,dst.remaining());
        else
            byteCount = in.read(readArray);
        if(byteCount <= 0)
            return byteCount;
        dst.put(readArray,dst.position(),byteCount);
        return byteCount;
    }
    
    public void close()
    { 
        try
        {
            socket.close();
        }catch (IOException ioe)
        {
            log.error(ioe,ioe);
        }
    }

    /**
     * @return Returns the controllConnection.
     */
    public boolean isControllConnection() {
        return controllConnection;
    }

    /**
     * @param controllConnection The controllConnection to set.
     */
    public void setControllConnection(boolean controllConnection) {
        this.controllConnection = controllConnection;
    }
}
