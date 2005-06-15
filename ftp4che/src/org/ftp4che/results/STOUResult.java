/*
 * Created on 11.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ftp4che.results;

import java.util.List;

import org.ftp4che.Result;

/**
 * @author incubus
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class STOUResult extends ResultImpl implements Result {
    
    public STOUResult() {
    }

    public STOUResult(List result, int resultValue)
    {
        setResultLines(result);
        setResultValue(resultValue);
    }
}
