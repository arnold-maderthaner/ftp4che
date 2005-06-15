/**
 * Created on 11.06.2005
 * @author kurt
 */
package org.ftp4che.results;

import java.util.List;

import org.ftp4che.Result;


public class RNFRResult extends ResultImpl implements Result {
    
    public RNFRResult() {
    }

    public RNFRResult(List result, int resultValue)
    {
        setResultLines(result);
        setResultValue(resultValue);
    }
}
