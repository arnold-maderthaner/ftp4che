package org.ftp4che.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.ftp4che.commands.Command;
import org.ftp4che.commands.ListCommand;
import org.ftp4che.commands.RetrieveCommand;
import org.ftp4che.commands.StoreCommand;
import org.ftp4che.reply.ControlReply;
import org.ftp4che.reply.Reply;

public class ReplyWorker extends Thread {

    public static final int FINISHED = 1;
    public static final int UNKNOWN = -1;
    
    SocketChannel socketChannel;
    Command command;
    List<String> lines = new ArrayList<String>();
    Charset charset = Charset.forName( "ISO8859-1" );
    CharsetDecoder charDecoder = charset.newDecoder();
    
    private int status = ReplyWorker.UNKNOWN;
    
    
    public ReplyWorker ( SocketChannel sc, Command command ) {
        setSocketChannel( sc );
        setCommand ( command );
    }
    
    public static Reply readReply ( SocketChannel socketChannel ) {
        List<String> lines = new ArrayList<String>();
        Charset charset = Charset.forName( "ISO8859-1" );
        CharsetDecoder charDecoder = charset.newDecoder();
        
        try {
            String output = "";
            String out = "";
            ByteBuffer buf = ByteBuffer.allocate(1024);
            int amount;
            buf.clear();
            socketChannel.socket().setKeepAlive(true);

            while ((amount = socketChannel.read(buf)) >= 0) {
                if (amount == 0) {
                    try {
                        sleep(50);
                    } catch (InterruptedException ie) {}
                    continue;
                }

                buf.flip();
                out = charDecoder.decode(buf).toString();
                output += out;
                buf.clear();
                
                String[] tmp = output.split("\n");

                if (tmp.length > 0 && tmp[tmp.length - 1].length() > 3 
                        && tmp[tmp.length - 1].endsWith("\r")
                         && tmp[tmp.length - 1].charAt(3) == ' '
                         && Pattern.matches("[0-9]+", tmp[tmp.length - 1].substring(0, 3))) {
                    String[] stringLines = output.split("\n");
                    
                    for ( String line : stringLines )
                        lines.add(line);

                    output = "";
                }
                try {
                    sleep(50);
                } catch (InterruptedException ie) {}
            }
        } catch (Exception e) {
          e.printStackTrace(System.err);
        }
        
        return new ControlReply( lines );
    }
    
    public void run () {
        if ( getCommand() == null )
            throw new IllegalArgumentException("Given command is null!");
        if ( getSocketChannel() == null )
            throw new IllegalArgumentException("Given connection is closed already!");
        
        if ( getCommand() instanceof ListCommand ) {
//       TODO: think about a own solution for reading LIST
//       instead of using control connection message read
            ReplyWorker.readReply( getSocketChannel() );
            setStatus( ReplyWorker.FINISHED );
        }else if ( getCommand() instanceof RetrieveCommand ) {
            RetrieveCommand retrieveCommand = (RetrieveCommand) getCommand();
            
            if ( retrieveCommand.getFromFile().getType().intern() == Command.TYPE_I ) {
 
            }else
                throw new IllegalArgumentException("Unknown file transfer type for download!");
            
            
            
        }else if ( getCommand() instanceof StoreCommand ) {
            StoreCommand storeCommand = (StoreCommand) getCommand();
            
            if ( storeCommand.getToFile().getType().intern() == Command.TYPE_I ) {
                
            }else
                throw new IllegalArgumentException("Unknown file transfer type for upload!");
            
        }else
            throw new IllegalArgumentException("Given command is not supported!");
    }

    /**
     * @param socketChannel The socketChannel to set.
     */
    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    /**
     * @return Returns the command.
     */
    public Command getCommand() {
        return command;
    }

    /**
     * @param command The command to set.
     */
    public void setCommand(Command command) {
        this.command = command;
    }

    /**
     * @return Returns the socketChannel.
     */
    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    /**
     * @return Returns the status.
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status The status to set.
     */
    public void setStatus(int status) {
        this.status = status;
    }
}
