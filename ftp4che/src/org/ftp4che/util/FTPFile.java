/*
 * Created on 12.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ftp4che.util;

import java.util.StringTokenizer;



/**
 * @author arnold
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FTPFile {
    String name = "", mode ="";
    long size = 0;
	boolean visible = true;
	String date = "";
	String parent = "";
	String user, group;


	/**
	 *  Constructor for the FtpFile object
	 *
	 *@param  name  Description of Parameter
	 */
	public FTPFile(String name) {
		this.name = name;
		if (name.startsWith(".")) {
			visible = false;
		} else {
			visible = true;
		}
	}

	public FTPFile() { }

	public String getDate() {
		return date;
	}

	public void setDate(String dateString) {
		date = dateString;
	}

	public void setName(String name) {
		this.name = name;
		if (name.startsWith(".")) {
			visible = false;
		} else {
			visible = true;
		}		
	}

	public void setSize(long size) {
		this.size = size;
	}


	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getName() {
		return name;
	}

	public long getSize() {
		return size;
	}

	public String getMode() {
		return mode;
	}

	public boolean isFile() {
		return mode.charAt(0) == '-';
	}

	public boolean isDirectory() {
		return mode.charAt(0) == 'd';
	}

	public boolean isLink() {
		return mode.charAt(0) == 'l';
	}

	public String toString() {
		return name;
	}

	public boolean isVisible() {
		return visible;
	}
	
	//-rw-r--r--    1 0        0         4046000 Jun 12 12:43 test.file
	public static FTPFile parseLine(String line)
	{
	    FTPFile file = new FTPFile();
	    StringTokenizer st = new StringTokenizer(line," ");
	    file.setMode(st.nextToken());
	    st.nextToken();
	    file.setUser(st.nextToken());
	    file.setGroup(st.nextToken());
	    file.setSize(Long.parseLong(st.nextToken()));
	    String date = "";
	    while(st.countTokens() > 1)
	    {
	        date += st.nextToken() + " ";
	    }
	    date = date.trim();
	    file.setDate(date);
	    file.setName(st.nextToken().trim());
	    return file;
	}
	
    /**
     * @return Returns the group.
     */
    public String getGroup() {
        return group;
    }
    /**
     * @param group The group to set.
     */
    public void setGroup(String group) {
        this.group = group;
    }
    /**
     * @return Returns the user.
     */
    public String getUser() {
        return user;
    }
    /**
     * @param user The user to set.
     */
    public void setUser(String user) {
        this.user = user;
    }
}
