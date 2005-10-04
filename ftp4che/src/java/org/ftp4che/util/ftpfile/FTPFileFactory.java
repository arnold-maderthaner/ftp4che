package org.ftp4che.util.ftpfile;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

public class FTPFileFactory {

    private static Logger log = Logger.getLogger(FTPFileFactory.class);

    public static final String UNIX_IDENTIFICATION = "UNIX";

    public static final String WINDOWS_IDENTIFICATION = "WINDOWS";

    public static final String VMS_IDENTIFICATION = "VMS";

    private String system;

    private FileParser parser = null;

    public FTPFileFactory(String system) {
        this.system = system.toUpperCase();
        parser = getParserInstance();
    }

    public void generateDateParsers(Locale locale) {
        parser.generateDateParsers(locale);
    }

    public String getSystem() {
        return system;
    }

    public FileParser getParserInstance() {
        if (system.indexOf(UNIX_IDENTIFICATION) > 0)
            return new UnixFileParser();
        else if (system.indexOf(WINDOWS_IDENTIFICATION) > 0)
            return new WindowsFileParser();
        else if (system.indexOf(VMS_IDENTIFICATION) > 0) {
            return new VMSFileParser();
        } else {
            log.warn("Unknown SYST '" + system + "', using UnknownFileParser");
            return new UnknownFileParser();
        }
    }

    public List<FTPFile> parse(List<String> serverLines, String parentPath)
            throws ParseException {
        List<FTPFile> files = new ArrayList<FTPFile>(serverLines.size());
        for (String line : serverLines) {
            FTPFile file = parser.parse(line, parentPath);
            if (file != null)
                files.add(file);
        }
        return files;
    }

    // /**
    // * Parse an array of raw file information returned from the
    // * FTP server
    // *
    // * @param files array of strings
    // * @return array of FTPFile objects
    // */
    // public FTPFile[] parse(String[] files) throws ParseException {
    //               
    // FTPFile[] temp = new FTPFile[files.length];
    //        
    // // quick check if no files returned
    // if (files.length == 0)
    // return temp;
    //                
    // int count = 0;
    // boolean checkedUnix = false;
    // for (int i = 0; i < files.length; i++) {
    // try {
    // if (files[i] == null || files[i].trim().length() == 0)
    // continue;
    //                
    // // swap to Unix if looks like Unix listing
    // if (!checkedUnix && parser != unix && UnixFileParser.isUnix(files[i])) {
    // parser = unix;
    // checkedUnix = true;
    // log.info("Swapping Windows parser to Unix");
    // }
    //                
    // FTPFile file = null;
    // if(usingVMS) {
    // // vms uses 2 lines for some file listings. send 2 just in case
    // if (files[i].indexOf(';') > 0) {
    // if (i+1 < files.length && files[i+1].indexOf(';') < 0) {
    // file = parser.parse(files[i] + " " + files[i+1]);
    // i++; // skip over second part for next iteration
    // }
    // else
    // file = parser.parse(files[i]);
    // }
    // }
    // else {
    // file = parser.parse(files[i]);
    // }
    // // we skip null returns - these are duff lines we know about and don't
    // // really want to throw an exception
    // if (file != null) {
    // temp[count++] = file;
    // }
    // }
    // catch (ParseException ex) {
    // log.info("Failed to parse listing '" + files[i] + "': " +
    // ex.getMessage());
    // if (rotateParsers) { // first error, let's try swapping parsers
    // rotateParsers = false; // only do once
    // rotateParsers();
    // FTPFile file = parser.parse(files[i]);
    // if (file != null)
    // temp[count++] = file;
    // }
    // else // rethrow
    // throw ex;
    // }
    // }
    // FTPFile[] result = new FTPFile[count];
    // System.arraycopy(temp, 0, result, 0, count);
    // return result;
    // }

}
