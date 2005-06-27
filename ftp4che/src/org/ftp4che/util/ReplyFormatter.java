
package org.ftp4che.util;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ftp4che.exception.UnkownReplyStateException;
import org.ftp4che.reply.Reply;

public class ReplyFormatter {
    public static String parsePWDReply(Reply pwdReply) throws UnkownReplyStateException
    {
        List<String> lines = pwdReply.getLines();
        if(lines.size() != 1)
            throw new UnkownReplyStateException("PWD Reply has to have a size of 1 entry but it has: " + lines.size());
        String line = lines.get(0);
        //LINE: 257 "/" is current directory.
        return line.substring(line.indexOf('"') + 1,line.lastIndexOf('"'));     
    }

    public static List<FTPFile> parseListReply(Reply listReply) 
    {
        List<String> lines = listReply.getLines();
        List<FTPFile> parsedLines = new ArrayList<FTPFile>(lines.size());
        for(Iterator<String> it=lines.iterator();it.hasNext();)
        {
            parsedLines.add(FTPFile.parseLine(it.next()));
        }
        return parsedLines;
    }
    
    public static InetSocketAddress parsePASVCommand(Reply pasvReply) throws UnkownReplyStateException
    {
       List<String> lines = pasvReply.getLines();
        if(lines.size() != 1)
            throw new UnkownReplyStateException("PASV Reply has to have a size of 1 entry but it has: " + lines.size());
        String line = lines.get(0);
        line = line.substring(line.indexOf('(')+1,line.lastIndexOf(')'));
        String[] host = line.split(",");
        return new InetSocketAddress(host[0] + "." + host[1] + "." + host[2] + "." + host[3],(Integer.parseInt(host[5]) << 8) + Integer.parseInt(host[4]));
    }
}
