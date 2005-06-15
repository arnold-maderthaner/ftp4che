/**
 * Created on 11.06.2005
 * @author kurt
 */
package org.ftp4che.results;

import java.util.ArrayList;
import java.util.List;

import org.ftp4che.Result;
import org.ftp4che.util.FTPFile;


public class LISTResult extends ResultImpl implements Result {

    public LISTResult() {
    }

    public LISTResult(List result, int resultValue)
    {
        setResultLines(result);
        setResultValue(resultValue);
    }

    public List getFileList() {
        List fileList = new ArrayList();
        for(int i = 0; i < getResultLines().size(); i++)
            fileList.add(FTPFile.parseLine((String)getResultLines().get(i)));
        return fileList;
    }
}
