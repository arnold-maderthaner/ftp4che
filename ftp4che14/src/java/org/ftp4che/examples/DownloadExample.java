package org.ftp4che.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.ftp4che.FTPConnection;
import org.ftp4che.FTPConnectionFactory;
import org.ftp4che.exception.ConfigurationException;
import org.ftp4che.exception.FtpIOException;
import org.ftp4che.exception.FtpWorkflowException;
import org.ftp4che.util.ftpfile.FTPFile;

public class DownloadExample {

	public static void main(String args[]) {
		Logger log = Logger.getLogger("DownloadExample");
		if (args.length < 4) {
			log.error("Wrong number of arguments !");
			log.error("Usage: DownloadExample <-PASV/PORT> ");
			System.exit(-1);
		}
		Properties connectionProperties = new Properties();
		connectionProperties.setProperty("connection.type", "FTP_CONNECTION");
		connectionProperties.setProperty("connection.timeout", "10000");

		String fromDirectory = args[2];

		File toDirectory = new File(args[3]);
		
		if (args[0].equalsIgnoreCase("-PASV")) {
			connectionProperties.setProperty("connection.passive", "true");
		} else {
			connectionProperties.setProperty("connection.passive", "false");
		}
		try {
			URL url = new URL(args[1]);
			String userInfo = url.getUserInfo();
			connectionProperties.setProperty("connection.host", url.getHost());
			if (url.getPort() > 0)
				connectionProperties.setProperty("connection.port", url
						.getPort()
						+ "");
			if (userInfo != null) {
				log.debug(userInfo.substring(userInfo.indexOf(":")));
				connectionProperties.setProperty("user.login", userInfo
						.substring(0, userInfo.indexOf(":")));
				connectionProperties.setProperty("user.password", userInfo
						.substring(userInfo.indexOf(":") + 1));
			}
		} catch (MalformedURLException mue) {
			log.fatal("Couldn't parse URL: " + args[2]);
			log.fatal(mue, mue);
		}
		try {
			FTPConnection connection = FTPConnectionFactory
					.getInstance(connectionProperties);
			connection.connect();

			if (fromDirectory.endsWith("/")) {

				connection.downloadDirectory(new FTPFile(fromDirectory, true),
						new FTPFile(toDirectory));
			} else {
				FTPFile fromFile = new FTPFile(fromDirectory, false);
				connection.downloadFile(fromFile, new FTPFile(toDirectory));
			}
			connection.disconnect();
		} catch (FileNotFoundException ffe) {
			ffe.printStackTrace();
		} catch (ConfigurationException ce) {
			log.fatal(ce, ce);
		} catch (FtpIOException ftpioe) {
			log.fatal(ftpioe);
		} catch (FtpWorkflowException ftpwe) {
			log.fatal(ftpwe);
		} catch (IOException ioe) {
			log.fatal(ioe);
		}
	}

}
