package org.ftp4che;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ftp4che.exception.FtpIOException;
import org.ftp4che.exception.FtpWorkflowException;
import org.ftp4che.util.ftpfile.FTPFile;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the directory listing functions of {@link FTPConnection}.
 * <P>
 * This test relies on the existence of  some well-known files and directories
 * in the default FTP server defined at {@link AbstractFtpConnectionTest#FTP_HOST}.
 */
public class FtpListingTest extends AbstractFtpConnectionTest {

	@Test
	public void shouldListRootDirectory() throws IOException, FtpWorkflowException, FtpIOException {
		connect();

	    Assert.assertEquals("/", ftpConnection.getWorkDirectory());
		List<FTPFile> listing = ftpConnection.getDirectoryListing();
		Assert.assertFalse(listing.isEmpty());

		assertContainsDirectories(listing, "/", "incoming", "pub");
		assertContainsFiles(listing, "/", "speedtest", "speedtest-5gb",
							"speedtest-5kb", "speedtest-5mb", "speedtest2");

		disconnect();
	}

	@Test
	public void shouldChangePathAndListDirectory() throws IOException, FtpWorkflowException, FtpIOException {
		connect();

	    Assert.assertEquals("/", ftpConnection.getWorkDirectory());
	    ftpConnection.changeDirectory("pub");
	    Assert.assertEquals("/pub", ftpConnection.getWorkDirectory());
		List<FTPFile> listing = ftpConnection.getDirectoryListing();
		Assert.assertFalse(listing.isEmpty());

		assertContainsDirectories(listing, "/pub", "customers", "java");
		assertContainsFiles(listing, "/pub", "AntiLirva.exe");

	    Assert.assertEquals("/pub", ftpConnection.getWorkDirectory());

		disconnect();
	}

	@Test
	public void shouldListSubDirectory() throws IOException, FtpWorkflowException, FtpIOException {
		connect();

	    Assert.assertEquals("/", ftpConnection.getWorkDirectory());
		List<FTPFile> listing = ftpConnection.getDirectoryListing("pub");
		Assert.assertFalse(listing.isEmpty());

		assertContainsDirectories(listing, "/pub", "customers", "java");
		assertContainsFiles(listing, "/pub", "AntiLirva.exe");

	    Assert.assertEquals("/", ftpConnection.getWorkDirectory());


		disconnect();
	}

	@Test
	public void shouldChangePathAndListSubDirectory() throws IOException, FtpWorkflowException, FtpIOException {
		connect();

	    ftpConnection.changeDirectory("pub");
	    Assert.assertEquals("/pub", ftpConnection.getWorkDirectory());
		List<FTPFile> listing = ftpConnection.getDirectoryListing("java");
		Assert.assertFalse(listing.isEmpty());
		assertContainsFiles(listing, "/pub/java", "JDK-1_0-win32-x86.exe");
	    Assert.assertEquals("/pub", ftpConnection.getWorkDirectory());

		disconnect();
	}

	private void assertContainsDirectories(List<FTPFile> ftpFiles, String prefix, String... directories) {
		Set<String> directoryNames = new HashSet<String>();
		for (FTPFile file: ftpFiles) {
			if (file.isDirectory()) {
				directoryNames.add(file.getName());
				Assert.assertTrue("Expected path to start with prefix " + prefix + " but was " + file.getPath(),
						file.getPath().startsWith(prefix));
			}
		}
		Assert.assertTrue("Expected " + Arrays.toString(directories) + " but found " + directoryNames,
				directoryNames.containsAll(Arrays.asList(directories)));
	}

	private void assertContainsFiles(List<FTPFile> ftpFiles, String prefix, String... files) {
		Set<String> fileNames = new HashSet<String>();
		for (FTPFile file: ftpFiles) {
			if (!file.isDirectory()) {
				fileNames.add(file.getName());
				Assert.assertTrue("Expected path to start with prefix " + prefix + " but was " + file.getPath(),
						file.getPath().startsWith(prefix));
			}
		}
		Assert.assertTrue("Expected " + Arrays.toString(files) + " but found " + fileNames,
				fileNames.containsAll(Arrays.asList(files)));
	}

}
