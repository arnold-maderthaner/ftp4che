package org.ftp4che.util.ftpfile;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;


public class UnixFileParser implements FileParser 
{
	private static final Logger log = Logger.getLogger(UnixFileParser.class);
    private static final char SYMBOLIC_LINK_IDENTIFICATION = 'l';
    private static final String SYMBOLIC_LINK_NAME = "->";
    private static final String DATE_FORMAT_STRING1 = "MMM-dd-yyyy";
    private static final String DATE_FORMAT_STRING2 = "MMM-dd-yyyy-HH:mm";
	private static final char NORMAL_FILE_IDENTIFICATION = '-';
	private static final char NORMAL_DIRECTORY_IDENTIFICATION = 'd';
    private static final int MIN_COUNT = 8;
    private SimpleDateFormat formatter1;
    private SimpleDateFormat formatter2;


    
    public UnixFileParser() {
    	generateDateParsers(Locale.getDefault());
    }
    
    public void generateDateParsers(Locale locale) {
        formatter1 = new SimpleDateFormat(DATE_FORMAT_STRING1,locale);
        formatter2 = new SimpleDateFormat(DATE_FORMAT_STRING2,locale);
    }  
    
    public static boolean isValidLine(String serverLine) {
        char ch = serverLine.charAt(0);
        if (ch == NORMAL_DIRECTORY_IDENTIFICATION || ch == NORMAL_FILE_IDENTIFICATION || ch == SYMBOLIC_LINK_IDENTIFICATION)
            return true;
        return false;
    }
    
    public FTPFile parse(String serverLine,String parentDirectory) throws ParseException 
    { 
        if (!isValidLine(serverLine))
            return null;
        
        StringTokenizer st = new StringTokenizer(serverLine," ");
        String[] fields = new String[st.countTokens()];
        int k = 0;
        while(st.hasMoreTokens())
        {
        	fields[k++] = st.nextToken();
        }

        if (MIN_COUNT > fields.length) {
        	throw new ParseException("Wrong number of fields: " + fields.length + " expected minimum:" + MIN_COUNT, 0);
        }
        int index = 0;
 
        // modes
        
        String mode = fields[index++];
        char ch = mode.charAt(0);
        boolean directory = false;
        boolean link = false;
        if (ch == NORMAL_DIRECTORY_IDENTIFICATION)
        	directory = true;
        else if (ch == SYMBOLIC_LINK_IDENTIFICATION)
            link = true;
        
        // lookup for the linkCount
        int linkCount = 0;
        if (Character.isDigit(fields[index].charAt(0))) {
            try {
                linkCount = Integer.parseInt(fields[index++]);
            }
            catch (NumberFormatException ignore) {}
        }
        
        // owners and groups
        String owner = fields[index++];
        String group = fields[index++];
        
        // size
        long size = 0L;
        String sizeString = fields[index];
        // some listings don't have group - make group -> size in
        // this case, and use the sizeStr for the start of the date
        if (!Character.isDigit(sizeString.charAt(0)) && Character.isDigit(group.charAt(0))) {
            sizeString = group;  
            group = "";
        }
        else {
            index++; 
        }
        try {
            size = Long.parseLong(sizeString);
        }
        catch (NumberFormatException ex) {
            throw new ParseException("Couldn't parse size from string: " + sizeString, 0);
        }
        int dateTimePos = index;
        Date date = null;
        StringBuffer dateTime = new StringBuffer(fields[index++]);
        dateTime.append('-').append(fields[index++]).append('-');
        
        String field = fields[index++];
        if (field.indexOf(':') > 0) {
        	 Calendar cal = Calendar.getInstance();
             int year = cal.get(Calendar.YEAR);
             dateTime.append(year).append('-').append(field);
             try
             {
            	 date = formatter2.parse(dateTime.toString());
             }catch(ParseException pe)
             {
            	 //Workaround if your locale is != ENGLISH
            	 formatter2 = new SimpleDateFormat(DATE_FORMAT_STRING2,Locale.ENGLISH);
            	 date = formatter2.parse(dateTime.toString());
             }

             if (date.after(cal.getTime())) {
                 cal.setTime(date);
                 cal.add(Calendar.YEAR, -1);
                 date = cal.getTime();
             }
        }
        else { 
        	   dateTime.append(field);
               date = formatter1.parse(dateTime.toString());
        }
            
        String name = null;
        String linkedname = null;
        
        int pos = 0;
        boolean ok = true;
        for (int i = dateTimePos; i < dateTimePos+3; i++) {
            pos = serverLine.indexOf(fields[i], pos);
            if (pos < 0) {
                ok = false;
                break;
            }
            else 
            {
                pos += fields[i].length();
            }
        }
        if (ok) {
            String remainder = serverLine.substring(pos).trim();
            if (!link) 
                name = remainder;
            else {
                pos = remainder.indexOf(SYMBOLIC_LINK_NAME);
                if (pos <= 0) 
                {
                    name = remainder;
                }
                else { 
                    int len = SYMBOLIC_LINK_NAME.length();
                    name = remainder.substring(0, pos).trim();
                    if (pos+len < remainder.length())
                        linkedname = remainder.substring(pos+len);
                }
            }
        }
        else {
            throw new ParseException("Couldn't parse file name from string: " + serverLine, 0);  
        }
        
        FTPFile file = new FTPFile(FTPFile.UNIX, parentDirectory,name,serverLine);
        file.setDate(date);
        file.setSize(size);
        file.setDirectory(directory);
        file.setOwner(owner);
        file.setMode(mode);
        file.setGroup(group);
        file.setLinkCount(linkCount);
        file.setLink(link);
        file.setLinkedName(linkedname);
       
        return file;
    }    
}
