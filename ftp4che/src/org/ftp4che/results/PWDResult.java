/**
 * Created on 11.06.2005
 * @author kurt
 */
package org.ftp4che.results;

import java.util.List;

import org.ftp4che.Result;


public class PWDResult extends ResultImpl implements Result {
  
    public PWDResult() {
    }

    public PWDResult(List result, int resultValue)
    {
        setResultLines(result);
        setResultValue(resultValue);
    }
}
