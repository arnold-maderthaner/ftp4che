package org.ftp4che.commands;

import org.ftp4che.reply.Reply;

public class RetrieveCommand extends Command implements DataConnectionCommand {

    public RetrieveCommand(String command)
    {
        super(command,new String[0]);
    }
    public RetrieveCommand(String command, String ... parameter)
    {
       super(command,parameter);
    }
    
    public Reply fetchDataConnectionReply() {
        // TODO Auto-generated method stub
        return null;
    }

}
