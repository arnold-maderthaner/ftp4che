/**
 * Created on 12.06.2005
 * @author kurt
 */
package org.ftp4che.results;

import java.util.List;

import org.ftp4che.Result;


public class SMNTResult extends ResultImpl implements Result {
 
    public SMNTResult() {
    }

    public SMNTResult(List result, int resultValue)
    {
        setResultLines(result);
        setResultValue(resultValue);
    }
}