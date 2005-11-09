/**                                                                         
 *  This file is part of ftp4che.                                            
 *  This library is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as published
 *  by the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.ftp4che.util.ftpfile;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

public class NetwareFileParser implements FileParser {
	private static final char NORMAL_FILE_IDENTIFICATION = '-';

    private static final char NORMAL_DIRECTORY_IDENTIFICATION = 'd';

    private static final String DATE_FORMAT_STRING1 = "MMM dd HH:mm";

    private SimpleDateFormat formatter;
    
    private Locale locale;
    
    public NetwareFileParser()
    {
    	 setLocale(Locale.getDefault());
    }
    
	/**
	 * d [R----F--] supervisor            512       Jan 16 18:53    login
	 * - [R----F--] rhesus             214059       Oct 20 15:27    cx.ex
	 */
	public FTPFile parse(String serverString, String parentDirectory)
			throws ParseException {
		if(serverString.charAt(0) != NORMAL_DIRECTORY_IDENTIFICATION &&
				serverString.charAt(0) != NORMAL_FILE_IDENTIFICATION)
			return null;
		boolean directory = (serverString.charAt(0) == NORMAL_DIRECTORY_IDENTIFICATION);
		StringTokenizer st = new StringTokenizer(serverString," ");
		//First Token is d / - 
		st.nextToken();
		String modes = st.nextToken();
		if(modes.indexOf("-") < 0 || modes.indexOf("R") < 0 || modes.indexOf("F") < 0)
			throw new ParseException("Couldn't identify second token as mode token: " + modes,0);
		if(modes.charAt(0) == '[' && modes.charAt(modes.length() - 1)  == ']')
			modes = modes.substring(1,modes.length() - 1);
		String user = st.nextToken();
		long size = Long.parseLong(st.nextToken());
		String dateToken = "";
		while(st.countTokens() > 1)
		{
			dateToken += " " + st.nextToken();
		}
		dateToken = dateToken.substring(1);
		Date date = null;
		try
		{
			formatter = new SimpleDateFormat(DATE_FORMAT_STRING1, locale);
			date = formatter.parse(dateToken);
		}catch (ParseException pe)
		{
			//Try other formatters here !
			formatter = new SimpleDateFormat(DATE_FORMAT_STRING1, Locale.ENGLISH);
			date = formatter.parse(dateToken);
			setLocale(Locale.ENGLISH);
		}
		String file = st.nextToken();
		FTPFile ftpFile = new FTPFile(FTPFile.NETWARE,parentDirectory,file,serverString);
		ftpFile.setSize(size);
		ftpFile.setDate(date);
		ftpFile.setMode(modes);
		ftpFile.setOwner(user);
		return ftpFile;
	}

	public void setLocale(Locale locale) {
		// TODO Auto-generated method stub
		this.locale = locale;
		
	}

}
