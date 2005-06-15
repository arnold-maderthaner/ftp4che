/*
 * Created on 11.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ftp4che;

import org.ftp4che.commands.ABORCommand;
import org.ftp4che.commands.ALLOCommand;
import org.ftp4che.commands.APPECommand;
import org.ftp4che.commands.CDUPCommand;
import org.ftp4che.commands.CWDCommand;
import org.ftp4che.commands.DELECommand;
import org.ftp4che.commands.HELPCommand;
import org.ftp4che.commands.LISTCommand;
import org.ftp4che.commands.LoginCommand;
import org.ftp4che.commands.MKDCommand;
import org.ftp4che.commands.MODECommand;
import org.ftp4che.commands.NLSTCommand;
import org.ftp4che.commands.NOOPCommand;
import org.ftp4che.commands.PASVCommand;
import org.ftp4che.commands.PORTCommand;
import org.ftp4che.commands.PWDCommand;
import org.ftp4che.commands.QUITCommand;
import org.ftp4che.commands.REINCommand;
import org.ftp4che.commands.RESTCommand;
import org.ftp4che.commands.RETRCommand;
import org.ftp4che.commands.RMDCommand;
import org.ftp4che.commands.RNFRCommand;
import org.ftp4che.commands.RNTOCommand;
import org.ftp4che.commands.SITECommand;
import org.ftp4che.commands.SMNTCommand;
import org.ftp4che.commands.STATCommand;
import org.ftp4che.commands.STORCommand;
import org.ftp4che.commands.STOUCommand;
import org.ftp4che.commands.STRUCommand;
import org.ftp4che.commands.SYSTCommand;
import org.ftp4che.commands.TYPECommand;
import org.ftp4che.results.ABORResult;
import org.ftp4che.results.ALLOResult;
import org.ftp4che.results.APPEResult;
import org.ftp4che.results.CDUPResult;
import org.ftp4che.results.CWDResult;
import org.ftp4che.results.DELEResult;
import org.ftp4che.results.HELPResult;
import org.ftp4che.results.LISTResult;
import org.ftp4che.results.LoginResult;
import org.ftp4che.results.MKDResult;
import org.ftp4che.results.MODEResult;
import org.ftp4che.results.NLSTResult;
import org.ftp4che.results.NOOPResult;
import org.ftp4che.results.PASVResult;
import org.ftp4che.results.PORTResult;
import org.ftp4che.results.PWDResult;
import org.ftp4che.results.QUITResult;
import org.ftp4che.results.REINResult;
import org.ftp4che.results.RESTResult;
import org.ftp4che.results.RETRResult;
import org.ftp4che.results.RMDResult;
import org.ftp4che.results.RNFRResult;
import org.ftp4che.results.RNTOResult;
import org.ftp4che.results.SITEResult;
import org.ftp4che.results.SMNTResult;
import org.ftp4che.results.STATResult;
import org.ftp4che.results.STORResult;
import org.ftp4che.results.STOUResult;
import org.ftp4che.results.STRUResult;
import org.ftp4che.results.SYSTResult;
import org.ftp4che.results.TYPEResult;

/**
 * @author arnold
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ResultFactory {
    public static Result getResultForCommand(Command cmd)
    {
        
        //TODO: ordnen nach haeufigkeit
        if(cmd instanceof LoginCommand)
            return new LoginResult();
        else if(cmd instanceof CWDCommand)
            return new CWDResult();
        else if (cmd instanceof PWDCommand)
            return new PWDResult();
        else if(cmd instanceof QUITCommand)
            return new QUITResult();
        else if(cmd instanceof CDUPCommand)
            return new CDUPResult();
        else if(cmd instanceof ABORCommand)
            return new ABORResult();
        else if(cmd instanceof ALLOCommand)
            return new ALLOResult();
        else if(cmd instanceof APPECommand)
            return new APPEResult();
        else if(cmd instanceof DELECommand)
            return new DELEResult();
        else if(cmd instanceof HELPCommand)
            return new HELPResult();
        else if(cmd instanceof LISTCommand)
            return new LISTResult();
        else if(cmd instanceof MKDCommand)
            return new MKDResult();
        else if(cmd instanceof MODECommand)
            return new MODEResult();
        else if(cmd instanceof NLSTCommand)
            return new NLSTResult();
        else if(cmd instanceof NOOPCommand)
            return new NOOPResult();
        else if(cmd instanceof PASVCommand)
            return new PASVResult();
        else if(cmd instanceof PORTCommand)
            return new PORTResult();
        else if(cmd instanceof REINCommand)
            return new REINResult();
        else if(cmd instanceof RESTCommand)
            return new RESTResult();
        else if(cmd instanceof RETRCommand)
            return new RETRResult();
        else if(cmd instanceof RMDCommand)
            return new RMDResult();
        else if(cmd instanceof RNFRCommand)
            return new RNFRResult();
        else if(cmd instanceof RNTOCommand)
            return new RNTOResult();
        else if(cmd instanceof SITECommand)
            return new SITEResult();
        else if(cmd instanceof SMNTCommand)
            return new SMNTResult();
        else if(cmd instanceof STATCommand)
            return new STATResult();
        else if(cmd instanceof STORCommand)
            return new STORResult();
        else if(cmd instanceof STOUCommand)
            return new STOUResult();
        else if(cmd instanceof STRUCommand)
            return new STRUResult();
        else if(cmd instanceof SYSTCommand)
            return new SYSTResult();
        else if(cmd instanceof TYPECommand)
            return new TYPEResult();
        return null;
    }

}
