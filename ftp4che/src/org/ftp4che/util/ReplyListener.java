/*
 * Created on 11.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ftp4che.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * @author arnold
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ReplyListener extends Thread {
    Logger log = null;
    
	int retrydelay;	
	SocketProvider socketProvider = null;

	private Charset charset;
	private CharsetDecoder decoder;
	volatile String line;
	List lines;
	volatile int resultValue = 0;
	volatile boolean quit;
	volatile long startTime;
	long timeout;
	volatile boolean wait;

	
    /**
     * @return Returns the line.
     */
    public String getLine() {
        return line;
    }
    
    public void setLine(String line)
    {
        this.line = line;
    }
 
    /**
     * @return Returns the lines.
     */
    public List getLines() {
        return lines;
    }
 
    /**
     * @return Returns the ret.
     */
    public int getResultValue() {
        return resultValue;
    }
 

	
	public ReplyListener(SocketProvider socketProvider) {
		log = Logger.getLogger(ReplyListener.class.getName());

	    //TODO: checkCharsets !!!!!
//		charset = Charset.defaultCharset();
		charset = Charset.forName("ISO-8859-1");
		System.out.println("charset name: " + charset.displayName());
		System.out.println("charset: " + charset.toString());
		decoder = charset.newDecoder();
		//TODO: make this configurable
		retrydelay = 10;
		timeout =  10*1000;
		setSocketProvider(socketProvider);
		lines = new ArrayList();
		start();
	}

	public void run() {
		try {
			String output = "";
			String out = "";
			ByteBuffer buf = ByteBuffer.allocate(1024);
			int amount;
			buf.clear();
			getSocketProvider().socket().setKeepAlive(true);
			startTime = 0;
			while ((amount = getSocketProvider().read(buf)) >= 0) {
			    log.debug("Amount that i read: " + amount);
				while (wait)
				try {
					Thread.sleep(50);
				} catch (Exception e) {}
				if (amount == 0) {
					if (startTime != 0 && (System.currentTimeMillis() - startTime) > timeout) {
						throw new IOException("reply timed out!");
					}
					try {
						sleep(50);
					} catch (InterruptedException ie) {}
					continue;
				}
				startTime = 0;
				buf.flip();
				out = decoder.decode(buf).toString();
				log.debug(getSocketProvider() + " " + out);
				output += out;
				buf.clear();
				// test if it was the last line of a multi line reply
				String[] tmp = output.split("\n");
				//	System.out.print(tmp[tmp.length-1]);
				if (tmp.length > 0 && tmp[tmp.length - 1].length() > 3 
						&& tmp[tmp.length - 1].endsWith("\r")
						 && tmp[tmp.length - 1].charAt(3) == ' '
						 && Pattern.matches("[0-9]+", tmp[tmp.length - 1].substring(0, 3))) {
					String[] splittedLines = output.split("\n");
					line = splittedLines[splittedLines.length - 1];
					resultValue = Integer.parseInt(line.substring(0, 2));
					for(int i=0; i < splittedLines.length;i++)
					    lines.add(splittedLines[i]);
				 output = out = "";
				}
				log.debug("LAST LINE IN WHILE");
			}
		} catch (Exception e) {
		    log.error(e,e);
		}
		log.debug("THIS IS END OF THREAD");
	}

    /**
     * @return Returns the socketProvider.
     */
    private SocketProvider getSocketProvider() {
        return socketProvider;
    }
    /**
     * @param socketProvider The socketProvider to set.
     */
    public void setSocketProvider(SocketProvider socketProvider) {
        this.socketProvider = socketProvider;
    }
    /**
     * @param resultValue The resultValue to set.
     */
    public void setResultValue(int resultValue) {
        this.resultValue = resultValue;
    }
    /**
     * @return Returns the start.
     */
    public long getStartTime() {
        return startTime;
    }
    /**
     * @param start The start to set.
     */
    public void setStartTime(long start) {
        this.startTime = start;
    }
}
