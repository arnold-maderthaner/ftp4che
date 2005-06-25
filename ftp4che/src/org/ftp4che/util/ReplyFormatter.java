package org.ftp4che.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ftp4che.exception.UnkownReplyStateException;
import org.ftp4che.reply.Reply;

public class ReplyFormatter {
    public static String parsePWDReply(Reply pwdReply) throws UnkownReplyStateException
    {
        List lines = pwdReply.getLines();
        if(lines.size() != 1)
            throw new UnkownReplyStateException("PWD Reply has to have a size of 1 entry but it has: " + lines.size());
        String line = (String)lines.get(0);
        //LINE: 257 "/" is current directory.
        return line.substring(line.indexOf('"'),line.lastIndexOf('"'));     
    }

    public static List parseListReply(Reply listReply) 
    {
        List lines = listReply.getLines();
        List parsedLines = new ArrayList(lines.size());
        for(Iterator it=lines.iterator();it.hasNext();)
        {
            parsedLines.add(FTPFile.parseLine((String)it.next()));
        }
        return parsedLines;
    }
}
