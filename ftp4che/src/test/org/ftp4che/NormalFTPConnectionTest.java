package org.ftp4che;

import java.io.File;
import java.util.Properties;

import org.ftp4che.util.FTPFile;


import junit.framework.Assert;
import junit.framework.TestCase;

public class NormalFTPConnectionTest extends TestCase {
	
	private FTPConnection ftpConnectionPassive = null;
	private FTPConnection ftpConnectionPort = null;

	public NormalFTPConnectionTest(String name) {
		super(name);
	}
	
	public void testAll()
	{
		try
		{
			ftpConnectionPassive.connect();
		}catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
		Assert.assertEquals(FTPConnection.CONNECTED,ftpConnectionPassive.getConnectionStatus());
		
		FTPFile from = new FTPFile("/home/ftpuser/download/","testDownload");
		FTPFile toPassive = new FTPFile(new File("/tmp/downloadPassive"));
		FTPFile toPort = new FTPFile(new File("/tmp/downloadPort"));
		try
		{
			ftpConnectionPassive.downloadFile(from,toPassive);
		}catch(Exception e)
		{
			Assert.fail("Error downloading passive:" + e.getMessage());
		}
		try
		{
			ftpConnectionPort.connect();
		}catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
		Assert.assertEquals(FTPConnection.CONNECTED,ftpConnectionPort.getConnectionStatus());
		try
		{	
			ftpConnectionPort.downloadFile(from,toPort);
		}catch(Exception e)
		{
			Assert.fail("Error downloading port:" + e.getMessage());
		}
//		Assert.assertEquals(FTPConnection.IDLE,ftpConnectionPassive.getConnectionStatus());
//		Assert.assertEquals(FTPConnection.IDLE,ftpConnectionPort.getConnectionStatus());
	}
	
	protected void setUp()
    throws java.lang.Exception
    {
		Properties pt = new Properties();
		pt.setProperty("connection.host","saturn");
        pt.setProperty("connection.port","21");
        pt.setProperty("user.login","ftpuser");
        pt.setProperty("user.password","ftp4che");
        pt.put("connection.type", new Integer(FTPConnection.FTP_CONNECTION));
        pt.put("connection.timeout",new Integer(10000));
        pt.put("connection.passive",new Boolean(true));
        ftpConnectionPassive = FTPConnectionFactory.getInstance(pt);
        pt.put("connection.passive",new Boolean(false));
        ftpConnectionPort = FTPConnectionFactory.getInstance(pt);
	}

	protected void tearDown()
    throws java.lang.Exception
    {
		ftpConnectionPassive.disconnect();
		ftpConnectionPort.disconnect();
    }



}
