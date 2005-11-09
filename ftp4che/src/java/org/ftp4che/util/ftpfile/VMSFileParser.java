package org.ftp4che.util.ftpfile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

public class VMSFileParser implements FileParser {
	private static final Logger log = Logger.getLogger(VMSFileParser.class);

	private Locale locale;

	private SimpleDateFormat formatter;

	private final static String formatString[] = { "dd-MMM-yyyy HH:mm:ss",
			"dd-MMM-yyyy HH:mm" };

	private String cacheLine = "";

	public VMSFileParser(Locale locale) {
		this.locale = locale;
	}

	public FTPFile parse(String serverString, String parentDirectory)
			throws ParseException {
		if (cacheLine.length() > 0) {
			serverString = cacheLine.concat(serverString);
			cacheLine = "";
		}
		if (serverString.length() == 0 || serverString.startsWith("Total")
				|| serverString.startsWith("Directory")) {
			return null;
		}
		StringTokenizer st = new StringTokenizer(serverString, " ");
		if (st.countTokens() < 4) {
			cacheLine = serverString;
			return null;
		}
		String name = st.nextToken();
		if (name.lastIndexOf(";") <= 0
				|| !Character.isDigit(name.charAt(name.length() - 1))) {
			log.debug("Filename: " + name + " is not a valid filename");
			throw new ParseException("Filename: " + name
					+ " is not a valid filename", 0);
		} else {
			name = name.substring(0, name.lastIndexOf(";"));
		}
		boolean directory = false;
		if (name.endsWith("DIR")) {
			directory = true;
			name = name.substring(name.indexOf("DIR"));
		}
		long size = -1;
		String sizeString = st.nextToken();
		if (sizeString.indexOf("/") > 0) {
			sizeString = sizeString.substring(0, sizeString.indexOf("/") - 1);
		}
		size = Long.parseLong(sizeString) * 512 * 1024;
		Date date = null;
		String dateToken = st.nextToken();
		String timeToken = st.nextToken();
		boolean formatted = false;
		for (int i = 0; i < formatString.length; i++) {
			try {
				formatter = new SimpleDateFormat(formatString[i], locale);
				date = formatter.parse(dateToken + " " + timeToken);
				formatted = true;
			} catch (ParseException pe) {
				try
				{
					formatter = new SimpleDateFormat(formatString[i],
							Locale.ENGLISH);
					date = formatter.parse(dateToken + " " + timeToken);
					this.locale = Locale.ENGLISH;
					formatted = true;
				}catch(ParseException pe2)
				{
					//Ignore this exception
					formatted = false;
				}
			}
			if (formatted)
				break;
		}
		String groupOwner = st.nextToken();
		String group = null;
		String owner = null;
		if (groupOwner.indexOf(",") > 0) {
			group = groupOwner.substring(1, groupOwner.indexOf(",") );
			owner = groupOwner.substring(groupOwner.indexOf(",") + 1, groupOwner.length() - 1);
		} else {
			group = groupOwner.substring(1,groupOwner.length() - 1);
		}
		String mode = st.nextToken();
		if (mode.startsWith("(") && mode.endsWith(")")) {
			mode = mode.substring(1, mode.length() - 1);
		}
		FTPFile file = new FTPFile(FTPFile.VMS, parentDirectory, name,
				serverString);
		file.setSize(size);
		file.setDirectory(directory);
		file.setDate(date);
		file.setGroup(group);
		file.setOwner(owner);
		file.setMode(mode);
		return file;

	}

}
