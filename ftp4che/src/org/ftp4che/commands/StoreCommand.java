package org.ftp4che.commands;

import org.ftp4che.reply.Reply;

public class StoreCommand extends Command implements DataConnectionCommand {
    public StoreCommand(String command)
    {
        super(command,new String[0]);
    }
    public StoreCommand(String command, String ... parameter)
    {
       super(command,parameter);
    }
    public Reply fetchDataConnectionReply() {
        // TODO Auto-generated method stub
        return null;
    }
}
