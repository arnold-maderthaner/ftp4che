/*
 * Created on 11.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ftp4che.commands;

import java.util.ArrayList;
import java.util.List;

import org.ftp4che.Command;
import org.ftp4che.Result;

/**
 * @author arnold
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LoginCommand implements Command{
    String password,user,account;
    Result result[] = null;
    
    public LoginCommand(String user,String password,String account)
    {
        setUser(user);
        setPassword(password);
        setAccount(account);
    }

    /**
     * @return Returns the account.
     */
    public String getAccount() {
        return account;
    }
    /**
     * @param account The account to set.
     */
    public void setAccount(String account) {
        this.account = account;
    }
    /**
     * @return Returns the password.
     */
    public String getPassword() {
        return password;
    }
    /**
     * @param password The password to set.
     */
    public void setPassword(String password) {
        this.password = password;
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

    /* (non-Javadoc)
     * @see org.ftp4che.Command#setResult(org.ftp4che.Result)
     */
    public void setResult(Result[] result) {
       this.result = result;
    }

    /* (non-Javadoc)
     * @see org.ftp4che.Command#isError()
     */
    public boolean isError() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.ftp4che.Command#isFailure()
     */
    public boolean isFailure() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.ftp4che.Command#isSuccess()
     */
    public boolean isSuccess() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.ftp4che.Command#getResult()
     */
    public Result[] getResult() {
       return result;
    }
    
    public List getTextCommands()
    {
        List commands = new ArrayList();
        commands.add("USER " + getUser() + Command.delimiter);
        commands.add("PASS " + getPassword()  + Command.delimiter);
        if(getAccount() != null && getAccount().length() > 0)
            commands.add("ACCT " + getAccount()  + Command.delimiter);
        return commands;
    }
}
