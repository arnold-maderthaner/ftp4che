package org.ftp4che.util.ftpfile;

import java.text.ParseException;
import java.util.Locale;

public interface FileParser {
    public FTPFile parse(String serverString, String parentDirectory)
            throws ParseException;

    public void setLocale(Locale locale);
}
