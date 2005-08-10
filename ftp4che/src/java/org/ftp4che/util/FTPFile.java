/**                                                                         *
*  This file is part of ftp4che.                                            *
*                                                                           *
*  This library is free software; you can redistribute it and/or modify it  *
*  under the terms of the GNU General Public License as published    		*
*  by the Free Software Foundation; either version 2 of the License, or     *
*  (at your option) any later version.                                      *
*                                                                           *
*  This library is distributed in the hope that it will be useful, but      *
*  WITHOUT ANY WARRANTY; without even the implied warranty of               *
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU        *
*  General Public License for more details.                          		*
*                                                                           *
*  You should have received a copy of the GNU General Public		        *
*  License along with this library; if not, write to the Free Software      *
*  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA  *
*                                                                           *
*****************************************************************************/
package org.ftp4che.util;

import java.util.StringTokenizer;

import org.ftp4che.commands.Command;



/**
 * @author arnold
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FTPFile {
    String path = "", name = "", mode ="";
    long size = 0;
	boolean visible = true;
	String date = "";
	String parent = "";
	String user, group;
    String type = Command.TYPE_I;

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

    /**
     * @return Returns the type.
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type to set.
     */
    public void setType(String type) {
        this.type = type;
    }

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
