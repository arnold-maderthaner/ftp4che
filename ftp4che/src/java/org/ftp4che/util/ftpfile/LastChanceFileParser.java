package org.ftp4che.util.ftpfile;


import java.text.ParseException;
import java.util.Locale;

import org.apache.log4j.Logger;

public class LastChanceFileParser implements FileParser {
    public static Logger log = Logger.getLogger(LastChanceFileParser.class);
	private Locale locale;
	
	public LastChanceFileParser()
	{
		setLocale(Locale.getDefault());
	}
	
	public FTPFile parse(String serverString, String parentDirectory)
			throws ParseException {
        log.info("LIST reply line -> " + serverString + " parentDirectory -> " + parentDirectory);
		return new FTPFile("THIS SHOULD NEVER HAPPEN","THIS IS AN ERROR");
		
	}

	public void setLocale(Locale locale) {
		this.locale = locale;

	}

}
