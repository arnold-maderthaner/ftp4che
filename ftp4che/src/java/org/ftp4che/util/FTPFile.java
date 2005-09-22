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

import java.io.File;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.ftp4che.commands.Command;



/**
 * @author arnold
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FTPFile implements Comparable {
    private String path = "";
    private String name = "";
    private String mode ="";
    private long size = 0;
    private boolean visible = true;
    private String date = "";
    private String parent = "";
    private String user, group;
    private String type = Command.TYPE_I;
    
	/**
	 *  Constructor for the FtpFile object
	 *
	 *@param  name  Description of Parameter
	 */
	public FTPFile(String name) {
		int slashPos = name.lastIndexOf("/");
		
		if ( slashPos >= 0 ) {
			if ( name.endsWith("/") ) {
				setPath( name.substring(0, name.substring(0, name.length()-1).lastIndexOf("/")));
				setName( name.substring(name.substring(0, name.length()-1).lastIndexOf("/")));
				setMode("d");
			}else {
				setPath( name.substring(0, slashPos) );
				setName( name.substring(slashPos) );
			}
		}else
			setName( name );
	}
	
	public FTPFile(File file)
	{
		setName(file.getName());
		setPath(file.getParent());
        setSize(file.length());
		String mode = "";
		
		if(file.isFile())
			mode += "-";
		else
			mode += "d";
		if(file.canRead())
			mode += "r";
		else
			mode += "-";
		if(file.canWrite())
			mode += "w";
		else
			mode += "-";
		
		setMode(mode);
	}
	
	public FTPFile(String path, String name) {
		this.name = name;
		this.path = path;
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
//		return new File(getPath(), getName()).toString();
		if(path != null && path.length() > 0)
			if(path.charAt(path.length() -1 ) == '/')
				return path + name;
			else
				return path + "/" + name;
		return name;
	}

	public boolean isVisible() {
		return visible;
	}
	
	//-rw-r--r--    1 0        0         4046000 Jun 12 12:43 test.file
	//drwxr-xr-x    5 501      501          4096 Jul 05 07:41 .
	//211-drwxr-xr-x  14 ftp      ftp          4096 Dec  1  2004 pub
	public static FTPFile parseLine(String line)
	{
	    FTPFile file = new FTPFile();
	    StringTokenizer st = new StringTokenizer(line," ");
	    String mode = st.nextToken();
	    String date = "";
	    String fileName = "";
	    
	    if(Pattern.matches("[0-9]+", mode.substring(0, 3)))
	    	mode = mode.substring(3);
	    if(st.countTokens() < 8)
	    	return null;
	    file.setMode(mode);
	    st.nextToken();
	    file.setUser(st.nextToken());
	    file.setGroup(st.nextToken());
	    file.setSize(Long.parseLong(st.nextToken()));
	    
	    if(st.countTokens() > 3)
	    {
	        date = st.nextToken() + " " + st.nextToken() + " " + st.nextToken();
	    }
	    date = date.trim();
	    file.setDate(date);
	    
	    while (st.hasMoreTokens())
	    	fileName = (fileName.concat(" ")).concat(st.nextToken());
	    
	    file.setName(fileName.trim());
	    
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
    
    public File getFile() {
        return new File( getPath(), getName() );
    }
    
	public int compareTo(Object o) {
		FTPFile to = (FTPFile) o;
		
		if ( this.isDirectory() && to.isDirectory()  )
			return this.getName().compareTo(to.getName());
		else if ( this.isDirectory() && to.isDirectory() )
			return this.getName().compareTo(to.getName());
		else if (this.isDirectory())
			return 1;
		
		return 0;
	}
}
