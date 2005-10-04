package org.ftp4che.util.ftpfile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;



public class WindowsFileParser implements FileParser {
    private SimpleDateFormat formatter;
    private static final String DIR = "<DIR>";
    private static final int MIN_COUNT = 4;
    private static final String DATE_FORMAT_STRING1 = "MM-dd-yy hh:mma";
    
     public WindowsFileParser() {
    	 generateDateParsers(Locale.getDefault());
     }
     
    public void generateDateParsers(Locale locale) {
        formatter = new SimpleDateFormat(DATE_FORMAT_STRING1, locale);
    }    

    public FTPFile parse(String serverLine,String parentDirectory) throws ParseException {
    	StringTokenizer st = new StringTokenizer(serverLine," ");
        String[] fields = new String[st.countTokens()];
        int k = 0;
        while(st.hasMoreTokens())
        {
        	fields[k++] = st.nextToken();
        }
        
        if (MIN_COUNT > fields.length)
            throw new ParseException("Wrong number of fields: " + fields.length + " expected minimum:" + MIN_COUNT, 0);
        
        Date date = formatter.parse(fields[0] + " " + fields[1]);
        boolean directory = false;
        long size = 0L;
        if (fields[2].equalsIgnoreCase(DIR))
        	directory = true;
        else {
            try {
                size = Long.parseLong(fields[2]);
            }
            catch (NumberFormatException ex) {
                throw new ParseException("Failed to parse size: " + fields[2], 0);
            }
        }
        
        // we've got to find the starting point of the name. We
        // do this by finding the pos of all the date/time fields, then
        // the name - to ensure we don't get tricked up by a date or dir the
        // same as the filename, for example
        int pos = 0;
        boolean ok = true;
        for (int i = 0; i < 3; i++) {
            pos = serverLine.indexOf(fields[i], pos);
            if (pos < 0) {
                ok = false;
                break;
            }
            else { // move on the length of the field
                pos += fields[i].length();
            }
        }
        if (ok) {
            String name = serverLine.substring(pos).trim();
            FTPFile file = new FTPFile(FTPFile.WINDOWS,parentDirectory, name,serverLine); 
            file.setSize(size);
            file.setDate(date);
            file.setDirectory(directory);
            return file;
        }
        else {
            throw new ParseException("Failed to retrieve name: " + serverLine, 0);  
        }
    }
  
}
