package org.ftp4che;

import static junit.framework.Assert.fail;
import static junit.framework.Assert.assertEquals;

import java.util.Properties;

import org.junit.Before;

/**
 * @author kurt
 *
 * @description
 * Linux:
 * 	1) Configure a ftp server which is accessable on net.
 * 	2) Set up a socks4/5 server using following command: ssh -D 3128 user@host
 * 	3) Run the test
 */

public class AbstractFtpConnectionTest {
	
	// connect information
	protected static final String FTP_HOST = "ftp.inode.at";
	protected static final String FTP_PORT = "21";
	protected static final String FTP_USER = "anonymous";
	protected static final String FTP_PASS = "pass@test.com";
	
	protected static final String PROXY_HOST = "localhost";
	protected static final String PROXY_PORT = "3128";
	protected static final String PROXY_USER = "";
	protected static final String PROXY_PASS = "";
	
	protected Properties connectionProperties;
	protected FTPConnection ftpConnection;
	
	@Before
	public void initGenericConnection() throws Exception {
		connectionProperties = new Properties();

		connectionProperties.setProperty("connection.host", FTP_HOST);
		connectionProperties.setProperty("connection.port", FTP_PORT);
		connectionProperties.setProperty("user.login", FTP_USER);
		connectionProperties.setProperty("user.password", FTP_PASS);
		connectionProperties.put("connection.timeout", new Integer(1000));
	}
	
	protected void useSocks4() {
		connectionProperties.setProperty("proxy.type", "SOCKS4");
		connectionProperties.setProperty("proxy.host", PROXY_HOST);
		connectionProperties.setProperty("proxy.port", PROXY_PORT);
		connectionProperties.setProperty("proxy.user", PROXY_USER);
		connectionProperties.setProperty("proxy.pass", PROXY_PASS);
	}
	
	protected void useSocks5() {
		connectionProperties.setProperty("proxy.type", "SOCKS5");
		connectionProperties.setProperty("proxy.host", PROXY_HOST);
		connectionProperties.setProperty("proxy.port", PROXY_PORT);
		connectionProperties.setProperty("proxy.user", PROXY_USER);
		connectionProperties.setProperty("proxy.pass", PROXY_PASS);
	}
		
	protected void connect() {
		try {
			ftpConnection = FTPConnectionFactory.getInstance(connectionProperties);
		}catch(org.ftp4che.exception.ConfigurationException ce) {
			fail("Missconfigured ftp connection ...");
		}
		
		try {
			ftpConnection.connect();
		}catch(Exception e) {
			fail("Failed connecting to ftp server (" + e.getClass() + ")");
		}
		
		assertEquals(ftpConnection.getConnectionStatus(), FTPConnection.IDLE);
	}
	
	protected void disconnect() {
		try {
			ftpConnection.disconnect();
		}catch(Exception e) {
			fail("Failed disconnecting from ftp server (" + e.getClass() + ")");
		}
		
		assertEquals(ftpConnection.getConnectionStatus(), FTPConnection.DISCONNECTED);
	}
}
