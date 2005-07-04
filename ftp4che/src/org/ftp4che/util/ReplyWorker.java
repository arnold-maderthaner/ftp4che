package org.ftp4che.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.ftp4che.commands.Command;
import org.ftp4che.commands.ListCommand;
import org.ftp4che.commands.RetrieveCommand;
import org.ftp4che.commands.StoreCommand;
import org.ftp4che.reply.Reply;


public class ReplyWorker extends Thread {
	public Logger log = Logger.getLogger(ReplyWorker.class.getName());
    public static final int FINISHED = 1;
    public static final int ERROR_FILE_NOT_FOUND = 2;
    public static final int ERROR_IO_EXCEPTION = 3;
    public static final int UNKNOWN = -1;
    
    Exception caughtException = null;
    SocketProvider socketProvider;
    Command command;
    List<String> lines = new ArrayList<String>();
    Charset charset = Charset.forName( "ISO8859-1" );
    CharsetDecoder charDecoder = charset.newDecoder();
    ByteBuffer buffer = ByteBuffer.allocate(16384);
    
    private int status = ReplyWorker.UNKNOWN;
    Reply reply;
    
    
    public ReplyWorker ( SocketProvider sc, Command command ) {
        setSocketProvider( sc );
        setCommand ( command );
    }
    public static Reply readReply ( SocketProvider socketProvider) {
        return ReplyWorker.readReply(socketProvider,false);
    }
    public static Reply readReply ( SocketProvider socketProvider, boolean isListReply ) {
        List<String> lines = new ArrayList<String>();
        Charset charset = Charset.forName( "ISO8859-1" );
        CharsetDecoder charDecoder = charset.newDecoder();
        
        try {
            String output = "";
            String out = "";
            ByteBuffer buf = ByteBuffer.allocate(1024);
            int amount;
            buf.clear();
            socketProvider.socket().setKeepAlive(true);
            boolean read = true;
            while (read && (amount = socketProvider.read(buf)) >= 0) 
            {
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

                if (!isListReply && tmp.length > 0 && tmp[tmp.length - 1].length() > 3 
                        && tmp[tmp.length - 1].endsWith("\r")
                         && tmp[tmp.length - 1].charAt(3) == ' '
                         && Pattern.matches("[0-9]+", tmp[tmp.length - 1].substring(0, 3))) 
                {
                    String[] stringLines = output.split("\n");
                    
                    for ( String line : stringLines )
                        lines.add(line);
                    read = false;
                    output = "";
                    buf.clear();
                }
                else
                {
                    String[] stringLines = output.split("\n");
                    
                    for ( String line : stringLines )
                        lines.add(line);
                    output = "";
                    buf.clear();
                }
                try {
                    sleep(50);
                } catch (InterruptedException ie) {}
            }
            if(isListReply)
            {
                socketProvider.close();
            }
        } catch (Exception e) {
          e.printStackTrace(System.err);
        }
        
        return new Reply( lines );
    }
    
    public void run () {
        if ( getCommand() == null )
            throw new IllegalArgumentException("Given command is null!");
        if ( getSocketProvider() == null )
            throw new IllegalArgumentException("Given connection is closed already!");
        
        if ( getCommand() instanceof ListCommand ) 
        {
            setReply(ReplyWorker.readReply(getSocketProvider(),true));
            setStatus(ReplyWorker.FINISHED);
        }else if ( getCommand() instanceof RetrieveCommand ) 
        {
            RetrieveCommand retrieveCommand = (RetrieveCommand) getCommand();
            if ( retrieveCommand.getFromFile().getType().intern() == Command.TYPE_I ) 
            {
                try
                {
                    FileOutputStream out = new FileOutputStream(retrieveCommand.getToFile());
                    FileChannel channel = out.getChannel();
                    int amount;
                    try {
                        while ((amount = getSocketProvider().read(buffer)) != -1) 
                        {
                            if (amount == 0) 
                            {
                                try {
                                   Thread.sleep(4);
                                } catch (InterruptedException e) {}
                            } 
                            if (buffer.remaining() > 0) {
                                continue;
                            }
                            buffer.flip();
                            channel.write(buffer);
                            buffer.clear();
                        }
                        buffer.flip();
                        channel.write(buffer);
                        buffer.clear();
                        setStatus(ReplyWorker.FINISHED);
                        channel.close();
                        getSocketProvider().close();
                    } catch (IOException ioe) {
                        setCaughtException(ioe);
                        setStatus(ReplyWorker.ERROR_IO_EXCEPTION);
                    }
                   
                }catch (FileNotFoundException fnfe)
                {
                    setCaughtException(fnfe);
                    setStatus(ReplyWorker.ERROR_FILE_NOT_FOUND);
                }
            }else
                throw new IllegalArgumentException("Unknown file transfer type for download!"); 
        }
        else if ( getCommand() instanceof StoreCommand ) 
        {
            StoreCommand storeCommand = (StoreCommand) getCommand();
            
            if ( storeCommand.getToFile().getType().intern() == Command.TYPE_I )
            {
                try
                {
                	log.debug("Upload file: " + storeCommand.getFromFile().toString());
                    FileInputStream in = new FileInputStream(storeCommand.getFromFile());
                    FileChannel channel = in.getChannel();
                    int amount;
                    int socketWrite;
                    int socketAmount = 0;
                    try {
                        while ((amount = channel.read(buffer)) != -1) {
                            buffer.flip();
                            socketWrite = 0;
                            while ((socketWrite = getSocketProvider().write(buffer)) != -1) 
                            {
                                socketAmount += socketWrite;
                                if (amount <= socketAmount) {
                                    break;
                                }
                                if (socketWrite == 0) 
                                {
                                   try {
                                         Thread.sleep(4);
                                   } catch (InterruptedException e) {}
                                }
                            }
                            if (socketWrite == -1) {
                                break;
                            }
                            socketAmount = 0;
                            buffer.clear();        
                        }
                        setStatus(ReplyWorker.FINISHED);
                        channel.close();
                        getSocketProvider().close();
                    } catch (IOException ioe) {
                        setCaughtException(ioe);
                        setStatus(ReplyWorker.ERROR_IO_EXCEPTION);
                    }
                    
                }catch (FileNotFoundException fnfe)
                {
                    setCaughtException(fnfe);
                    setStatus(ReplyWorker.ERROR_FILE_NOT_FOUND);
                }
            }
            else
                throw new IllegalArgumentException("Unknown file transfer type for upload!");
            
        }else
            throw new IllegalArgumentException("Given command is not supported!");
    }

    /**
     * @param socketProvider The socketProvider to set.
     */
    public void setSocketProvider(SocketProvider socketProvider) {
        this.socketProvider = socketProvider;
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
     * @return Returns the socketProvider.
     */
    public SocketProvider getSocketProvider() {
        return socketProvider;
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

    /**
     * @return Returns the caughtException.
     */
    public Exception getCaughtException() {
        return caughtException;
    }

    /**
     * @param caughtException The caughtException to set.
     */
    public void setCaughtException(Exception caughtException) {
        this.caughtException = caughtException;
    }

	public Reply getReply() {
		return reply;
	}

	public void setReply(Reply reply) {
		this.reply = reply;
	}
}
