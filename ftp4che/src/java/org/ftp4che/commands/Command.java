/*
 * Created on 25.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ftp4che.commands;

/**
 * @author arnold
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Command {
    //Constants
    public final static String delimiter = "\r\n";
    
    public final static String TYPE_A = "TYPE A";
    public final static String TYPE_I = "TYPE I";
    
    public final static String ACCT = "ACCT";
    public final static String APPE = "APPE";
    public final static String CWD = "CWD";
    public final static String CDUP = "CDUP";
    public final static String DELE = "DELE";
    public final static String FEAT = "FEAT";
    public final static String MKD = "MKD";
    public final static String PASV = "PASV";
    public final static String PASS = "PASS";
    public final static String PORT = "PORT";
    public final static String PWD = "PWD";
    public final static String QUIT = "QUIT";
    public final static String RMD = "RMD";
    public final static String REST = "REST";
    public final static String RETR = "RETR";
    public final static String RNTO = "RNTO";
    public final static String RNFR = "RNFR";
    public final static String SITE = "SITE";
    public final static String STOR = "STOR";
    public final static String SYST = "SYST";
    public final static String USER = "USER";
    public final static String TYPE = "TYPE";
    public final static String LIST = "LIST";
    public final static String NOOP = "NOOP";
    public final static String STAT = "STAT -LA";
    public final static String PROT = "PROT";
    public final static String PBSZ = "PBSZ";
    
    
    String command;
    String[] parameter;

    public Command(String command)
    {
        this(command,"");
    }
    public Command(String command, String ... parameter)
    {
        setCommand(command);
        setParameter(parameter);
    }
    
    /**
     * @return Returns the command.
     */
    public String getCommand() {
        return command;
    }
    /**
     * @param command The command to set.
     */
    public void setCommand(String command) {
        this.command = command;
    }
    /**
     * @return Returns the parameter.
     */
    public String[] getParameter() {
        return parameter;
    }
    /**
     * @param parameter The parameter to set.
     */
    public void setParameter(String[] parameter) {
        this.parameter = parameter;
    }
    
    public String toString()
    {
        String returnValue = getCommand();
        for(String s: getParameter())
        {
            returnValue += " " + (s);
        }
        
        return returnValue.trim() + delimiter;
    }
    
}
