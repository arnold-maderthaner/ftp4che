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
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import org.apache.log4j.Logger;
import org.ftp4che.FTPConnection;

public class SocketProvider {

    private SSLSupport supporter;
	private int sslMode = FTPConnection.FTP_CONNECTION;
	private Socket socket = null;
	private static final Logger log = Logger.getLogger(SocketProvider.class.getName());
	private boolean isControllConnection = true;
	private OutputStream out = null;
	private InputStream in = null;
	private byte[] readArray = new byte[16384];
   
    
	public SocketProvider() {
		socket = new Socket();
	}
    
    public SocketProvider(boolean isControllConnection) throws IOException {
        this();
        setControllConnection(isControllConnection);
    }

    public SocketProvider(Socket socket, boolean isControllConnection ) throws IOException
    {
        this(socket, isControllConnection, FTPConnection.MAX_DOWNLOAD_BANDWIDTH, FTPConnection.MAX_UPLOAD_BANDWIDTH);
    }
    
//	public SocketProvider( Socket socket ) throws IOException{
//		this(socket, true, FTPConnection.MAX_DOWNLOAD_BANDWIDTH, FTPConnection.MAX_UPLOAD_BANDWIDTH);
//	}
    
    public SocketProvider( Socket socket, boolean isControllConnection, int maxDownload, int maxUpload ) throws IOException{
        setControllConnection(isControllConnection);
        
        this.socket = socket;
        if(out == null)
            out = new BandwidthControlledOutputStream(socket.getOutputStream(), maxUpload);
        if(in == null)
            in = new BandwidthControlledInputStream(socket.getInputStream(), maxDownload);
    }
    
//	public void connect( SocketAddress remote ) throws IOException {
//	    connect(remote, FTPConnection.MAX_DOWNLOAD_BANDWIDTH, FTPConnection.MAX_UPLOAD_BANDWIDTH);
//	}
    
    public void connect( SocketAddress remote, int maxDownload, int maxUpload ) throws IOException {
        socket.connect(remote);
        out = new BandwidthControlledOutputStream(socket.getOutputStream(), maxUpload);
        in = new BandwidthControlledInputStream(socket.getInputStream(), maxDownload);
    }
	
	public Socket socket() {
		return socket;
	}
	
	
	public boolean isConnected() {
		return socket.isConnected();
	}
    
    public boolean needsCrypt()
    {
       return this.sslMode != FTPConnection.FTP_CONNECTION; 
    }
	
	public void close() throws IOException {

        if (needsCrypt())
        {
            if ( supporter != null )
                 supporter.close();
        }        
        socket.close();
	}	

	public int write(ByteBuffer src) throws IOException 
	{
        if (needsCrypt())
        {
        	return supporter.write(src);
        	//throw new IOException("SSL NOT IMPLEMENTED YET");
        }
        int byteCount = src.remaining();
        out.write(src.array());
		return byteCount;
	}
	
	public int read( ByteBuffer dst ) throws IOException {
        if (needsCrypt())
        {
            return supporter.read(dst);
        }
        int byteCount = 0;
        if(isControllConnection())
        {
            byteCount = in.read(readArray,0,dst.remaining());
        }
        else
        {
            byteCount = in.read(readArray);
        }
        
        if(byteCount <= 0)
        	return byteCount;
        dst.put(readArray,dst.position(),byteCount);
        return byteCount;
	}
	
	
	public String toString() {
		return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
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
    
    public void negotiate() {
        try {
        	supporter = new SSLSupport(socket, getSSLMode(),isControllConnection());
            supporter.initEngineAndBuffers();
            supporter.handshake();
            //TODO: throw exception and handle it !!
        }catch (Exception e) {
            log.fatal(e,e);
        }
    }
    
}
