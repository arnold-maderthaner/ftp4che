package org.ftp4che.util.ftpfile;


import java.text.ParseException;
import java.util.Locale;

public class LastChanceFileParser implements FileParser {

	public FTPFile parse(String serverString, String parentDirectory)
			throws ParseException {
		// TODO Auto-generated method stub
		return new FTPFile("THIS SHOULD NEVER HAPPEN","THIS IS AN ERROR");
	}

	public void setLocale(Locale locale) {
		// TODO Auto-generated method stub

	}

}
