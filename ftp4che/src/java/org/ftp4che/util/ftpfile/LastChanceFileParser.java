package org.ftp4che.util.ftpfile;


import java.text.ParseException;
import java.util.Locale;

public class LastChanceFileParser implements FileParser {

	private Locale locale;
	
	public LastChanceFileParser()
	{
		setLocale(Locale.getDefault());
	}
	
	public FTPFile parse(String serverString, String parentDirectory)
			throws ParseException {
		return new FTPFile("THIS SHOULD NEVER HAPPEN","THIS IS AN ERROR");
		
	}

	public void setLocale(Locale locale) {
		this.locale = locale;

	}

}
