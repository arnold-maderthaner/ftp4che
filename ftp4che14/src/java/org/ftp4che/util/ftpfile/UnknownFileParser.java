package org.ftp4che.util.ftpfile;

import java.text.ParseException;
import java.util.Locale;

public class UnknownFileParser implements FileParser {

    public FTPFile parse(String serverLine, String parentDirectory)
            throws ParseException {
        // TODO Auto-generated method stub
        return new FTPFile(
                "UNKNOWN NOT IMPLEMENTED YET!! If you can read this look at ftp4che.sf.net and give us some response because this should never happen :)",
                "NOT IMPLEMENTED");
    }

    public void generateDateParsers(Locale locale) {
        // TODO Auto-generated method stub

    }

}
