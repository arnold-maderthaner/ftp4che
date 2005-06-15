/**
 * Created on 12.06.2005
 * @author kurt
 */
package org.ftp4che.results;

import java.util.List;

import org.ftp4che.Result;


public class SITEResult extends ResultImpl implements Result {
 
    public SITEResult() {
    }

    public SITEResult(List result, int resultValue)
    {
        setResultLines(result);
        setResultValue(resultValue);
    }
}