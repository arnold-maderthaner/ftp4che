package org.ftp4che.util.ftpfile;


import java.text.ParseException;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

public class EPLFFileParser implements FileParser {
	private static final Logger log = Logger.getLogger(EPLFFileParser.class);
	public static final char EPLF_START_CHAR = '+';
	
	public FTPFile parse(String serverString, String parentDirectory)
			throws ParseException {
		if(serverString.charAt(0) != '+')
			throw new ParseException("Not an EPLF LIST response (no '+' on line start)",0);
		serverString = serverString.substring(1);
		StringTokenizer st = new StringTokenizer(serverString,",");
		Date date = null;
		String fileName = "";
		String mode = "";
		long size = -1;
		boolean directory = false;
		while(st.hasMoreTokens())
		{
			String token = st.nextToken();
			switch(token.charAt(0))
			{
			case 'r': mode += "r--r--r--";
					  break;
			case '/': directory=true;
					  mode = "d" + mode;
					  break;
			case 's': size = Long.parseLong(token.substring(1));
					  break;
			case 'm': date = new Date(Long.parseLong(token.substring(1)) * 1000);
			          break;
			case '\011': fileName = token.trim();
					  break;
			default:
				      log.debug("skip unnessacry token: " + token);
					  break;
			}
		}
		if(mode.length() == 9)
			mode = "-" + mode;
		FTPFile file = new FTPFile(FTPFile.UNKNOWN,parentDirectory,fileName,serverString);
		file.setDate(date);
		file.setMode(mode);
		file.setDirectory(directory);
		file.setSize(size);
		return file;
	}

}
