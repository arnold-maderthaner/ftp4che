package org.ftp4che.util.ftpfile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

public class VMSFileParser implements FileParser {
    private final static String DIR = ".DIR";

    private final static String HDR = "Directory";

    private final static String TOTAL = "Total";

    private final int BLOCKSIZE = 512 * 1024;

    private final static int MIN_EXPECTED_FIELD_COUNT = 4;

    private final static String DATE_FORMAT_STRING1 = "dd-MMM-yyyy HH:mm:ss";

    private final static String DATE_FORMAT_STRING2 = "dd-MMM-yyyy HH:mm";

    private SimpleDateFormat formatter;
    
    private Locale locale;

    public VMSFileParser() {
        setLocale(Locale.getDefault());
    }

    /**
     *00README.TXT;1      2 30-DEC-1996 17:44 [SYSTEM] (RWED,RWED,RE,RE)
     *CORE.DIR;1          1  8-SEP-1996 16:09 [SYSTEM] (RWE,RWE,RE,RE)
     * 
     */
    public FTPFile parse(String serverLine, String parentDirectory)
            throws ParseException {
        StringTokenizer st = new StringTokenizer(serverLine, " ");
        String[] fields = new String[st.countTokens()];
        int k = 0;
        while (st.hasMoreTokens()) {
            fields[k++] = st.nextToken();
        }

        // skip blank lines
        if (fields.length <= 0)
            return null;
        // skip line which lists Directory
        if (fields.length >= 2 && fields[0].compareTo(HDR) == 0)
            return null;
        // skip line which lists Total
        if (fields.length > 0 && fields[0].compareTo(TOTAL) == 0)
            return null;
        // probably the remainder of a listing on 2nd line
        if (fields.length < MIN_EXPECTED_FIELD_COUNT)
            return null;

        // first field is name
        String name = fields[0];

        // make sure it is the name (ends with ';<INT>')
        int semiPos = name.lastIndexOf(';');
        // check for ;
        if (semiPos <= 0) {
            throw new ParseException("File version number not found in name '"
                    + name + "'", 0);
        }
        name = name.substring(0, semiPos);

        String afterSemi = fields[0].substring(semiPos + 1);
        Integer.parseInt(afterSemi);

        boolean directory = false;
        if (semiPos < 0) {
            semiPos = fields[0].length();
        }
        if (semiPos >= 4) {
            String tstExtnsn = fields[0].substring(semiPos - 4, semiPos);
            if (tstExtnsn.compareTo(DIR) == 0) {
                directory = true;
                name = name.substring(0, semiPos - 4);
            }
        }

        int slashPos = fields[1].indexOf('/');
        String sizeUsed = fields[1];
        if (slashPos > 0)
            sizeUsed = fields[1].substring(0, slashPos);
        long size = Long.parseLong(sizeUsed) * BLOCKSIZE;

        Date date = null;
        try {
        	formatter = new SimpleDateFormat(DATE_FORMAT_STRING1,locale);
            date = formatter.parse(fields[2] + " " + fields[3]);
        } catch (ParseException ex) {
        	try
        	{
        		formatter = new SimpleDateFormat(DATE_FORMAT_STRING1,Locale.ENGLISH);
        		date = formatter.parse(fields[2] + " " + fields[3]);
        		setLocale(Locale.ENGLISH);
        	}catch(ParseException ex2)
        	{
        		try
        		{
        			formatter = new SimpleDateFormat(DATE_FORMAT_STRING2,locale);
        			date = formatter.parse(fields[2] + " " + fields[3]);
        		}catch(ParseException ex3)
        		{
        			formatter = new SimpleDateFormat(DATE_FORMAT_STRING2,Locale.ENGLISH);
        			date = formatter.parse(fields[2] + " " + fields[3]);
        			setLocale(Locale.ENGLISH);
        		}
        	}
        	
        }

        String group = null;
        String owner = null;
        if (fields.length >= 5) {
            if (fields[4].charAt(0) == '['
                    && fields[4].charAt(fields[4].length() - 1) == ']') {
                int commaPos = fields[4].indexOf(',');
                if (commaPos < 0) {
                    group = fields[4].substring(1,fields[4].length()-1);
                }
                else
                {
                	group = fields[4].substring(1, commaPos);
                	owner = fields[4].substring(commaPos + 1,
                			fields[4].length() - 1);
                }
            }
        }

        String mode = null;
        if (fields.length >= 6) {
            if (fields[5].charAt(0) == '('
                    && fields[5].charAt(fields[5].length() - 1) == ')') {
                mode = fields[5].substring(1, fields[5].length() - 2);
            }
        }

        FTPFile file = new FTPFile(FTPFile.VMS, parentDirectory, name,
                serverLine);
        file.setSize(size);
        file.setDirectory(directory);
        file.setDate(date);
        file.setGroup(group);
        file.setOwner(owner);
        file.setMode(mode);
        return file;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

}
