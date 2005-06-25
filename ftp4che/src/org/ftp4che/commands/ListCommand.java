package org.ftp4che.commands;

import org.ftp4che.reply.Reply;

public class ListCommand extends Command implements DataConnectionCommand {
   
    public ListCommand(String parameter)
    {
        super(Command.LIST,parameter);
    }
   
    public ListCommand()
    {
        this(".");
    }
    
    public Reply fetchDataConnectionReply() {
        // TODO Auto-generated method stub
        return null;
    }

}
