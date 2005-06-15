/**
 * Created on 12.06.2005
 * @author kurt
 */
package org.ftp4che.results;

import java.util.List;

import org.ftp4che.Result;


public class REINResult extends ResultImpl implements Result {
 
    public REINResult() {
    }

    public REINResult(List result, int resultValue)
    {
        setResultLines(result);
        setResultValue(resultValue);
    }
}
