package org.ftp4che.examples;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.ftp4che.FTPConnection;
import org.ftp4che.FTPConnectionFactory;
import org.ftp4che.commands.Command;
import org.ftp4che.exception.AuthenticationNotSupportedException;
import org.ftp4che.exception.ConfigurationException;
import org.ftp4che.exception.FtpIOException;
import org.ftp4che.exception.FtpWorkflowException;
import org.ftp4che.util.ftpfile.FTPFile;

public class UltimateBugFinder {

	private static Logger log = Logger.getLogger("MAIN");
	private static List<Properties> connections;
	
	// local settings
	private static FTPFile homeDirectory = new FTPFile(new File("/tmp"));
	private static FTPFile testfile1mb = new FTPFile(new File("/tmp/1mb"));
	private static FTPFile testfile5mb = new FTPFile(new File("/tmp/5mb"));
	private static FTPFile testfile10mb = new FTPFile(new File("/tmp/10mb"));
	private static FTPFile testdir = new FTPFile(new File("/tmp/uploaddir"));
	
	public UltimateBugFinder(List<Properties> connections) {
		UltimateBugFinder.connections = connections;
	}
	
	private static void execute() {
		
		for ( Properties conf : connections ) {
			
			FTPConnection conn = null;
			try {
				conn = FTPConnectionFactory.getInstance(conf);
			}catch(ConfigurationException ce) {
				log.error("could not get an instance of FTPConnection!", ce);
			}
			
			try {
				conn.connect();
				
				conn.checkSystem();
				
				conn.sendCommand(new Command(Command.NOOP));
				conn.noOperation();
				
				conn.setTransferType(false);
				conn.setTransferType(true);
				
				conn.getWorkDirectory();

				conn.makeDirectory("testdir");
				conn.changeDirectory("testdir");
				conn.changeToParentDirectory();
				conn.removeDirectory("testdir");
				conn.makeDirectory("testdir");
				conn.removeDirectory(new FTPFile(conn.getWorkDirectory(), "testdir", true));

				
				conn.makeDirectory("test");
				conn.changeDirectory("test");
				conn.uploadFile(testfile1mb, new FTPFile(conn.getWorkDirectory(), "1mb", false));
				conn.uploadFile(testfile5mb, new FTPFile(conn.getWorkDirectory(), "5mb", false));
				conn.uploadFile(testfile10mb, new FTPFile(conn.getWorkDirectory(), "10mb", false));
				conn.downloadFile(new FTPFile(conn.getWorkDirectory(), "1mb", false), new FTPFile(new File(homeDirectory.getFile(), "downloaded_1mb")));
				conn.downloadFile(new FTPFile(conn.getWorkDirectory(), "5mb", false), new FTPFile(new File(homeDirectory.getFile(), "downloaded_5mb")));
				conn.downloadFile(new FTPFile(conn.getWorkDirectory(), "10mb", false), new FTPFile(new File(homeDirectory.getFile(), "downloaded_10mb")));
				conn.renameFile(new FTPFile(conn.getWorkDirectory(), "1mb", false), "1mb_renamed");
				conn.renameFile(new FTPFile(conn.getWorkDirectory(), "5mb", false), "5mb_renamed");
				conn.renameFile(new FTPFile(conn.getWorkDirectory(), "10mb", false), "10mb_renamed");
				conn.deleteFile(new FTPFile(conn.getWorkDirectory(), "1mb_renamed", false));
				conn.deleteFile(new FTPFile(conn.getWorkDirectory(), "5mb_renamed", false));
				conn.deleteFile(new FTPFile(conn.getWorkDirectory(), "10mb_renamed", false));
				conn.changeToParentDirectory();
				
				conn.uploadDirectory(testdir, new FTPFile(conn.getWorkDirectory(), "testdir", true));
				conn.changeDirectory("testdir");
				conn.getDirectoryListing();
				conn.getDirectoryListing("testdir");
				conn.getFastDirectoryListing();
				
				conn.changeToParentDirectory();
				
				conn.downloadDirectory(new FTPFile("/tmp", "testdir", true), new FTPFile(new File(homeDirectory.getFile(), "testdir_downloaded")));
				conn.deleteDirectory(new FTPFile("/tmp", "testdir", true));
				
				conn.sendSiteCommand("HELP");
				
//				fxpFile
//				fxpDirectory
				
				/* TODO:
				 * getFastDirectoryListing(String)
				 * removeDirectory(FTPFile) <--- notwendig ????
				 */
				
				conn.disconnect();
				
			}catch(AuthenticationNotSupportedException anse) {
				log.error("The used connect authentification method is not supported!", anse);
			}catch(FtpWorkflowException fwe) {
				log.error("Some exception concerning the FTP workflow occured!", fwe);
			}catch(FtpIOException fio) {
				log.error("An Ftp IO Error occured!", fio);
			}catch(IOException ioe) {
				log.error("An IO Error!", ioe);
			}
		}
	}

	public static void main(String[] args) {
		
		
		ArrayList<Properties> conns = new ArrayList<Properties>();
		
        Properties pt1 = new Properties();
        pt1.setProperty("connection.host","172.25.12.197");
        pt1.setProperty("connection.port","21");
        pt1.setProperty("user.login","ftpuser");
        pt1.setProperty("user.password","ftp4che");
        pt1.setProperty("connection.type", "FTP_CONNECTION");
        pt1.setProperty("connection.timeout", "10000");
        pt1.setProperty("connection.passive", "true");
        
        Properties pt2 = new Properties();
        pt1.setProperty("connection.host","172.25.12.197");
        pt1.setProperty("connection.port","23");
        pt1.setProperty("user.login","ftpuser");
        pt1.setProperty("user.password","ftp4che");
        pt1.setProperty("connection.type", "FTP_CONNECTION");
        pt1.setProperty("connection.timeout", "10000");
        pt1.setProperty("connection.passive", "true");
        
        Properties pt3 = new Properties();
        pt1.setProperty("connection.host","172.25.12.200");
        pt1.setProperty("connection.port","21");
        pt1.setProperty("user.login","ftpuser");
        pt1.setProperty("user.password","ftp4che");
        pt1.setProperty("connection.type", "FTP_CONNECTION");
        pt1.setProperty("connection.timeout", "10000");
        pt1.setProperty("connection.passive", "true");
        
        Properties pt4 = new Properties();
        pt1.setProperty("connection.host","172.25.13.186");
        pt1.setProperty("connection.port","21");
        pt1.setProperty("user.login","ftpuser");
        pt1.setProperty("user.password","ftp4che");
        pt1.setProperty("connection.type", "FTP_CONNECTION");
        pt1.setProperty("connection.timeout", "10000");
        pt1.setProperty("connection.passive", "true");
        
        Properties pt5 = new Properties();
        pt1.setProperty("connection.host","172.25.13.149");
        pt1.setProperty("connection.port","21");
        pt1.setProperty("user.login","ftpuser");
        pt1.setProperty("user.password","ftp4che");
        pt1.setProperty("connection.type", "FTP_CONNECTION");
        pt1.setProperty("connection.timeout", "10000");
        pt1.setProperty("connection.passive", "true");
        
        conns.add(pt1);
        conns.add(pt2);
        conns.add(pt3);
        conns.add(pt4);
        conns.add(pt5);
        
        UltimateBugFinder ubf = new UltimateBugFinder(conns);
		
		execute();
	}
}
