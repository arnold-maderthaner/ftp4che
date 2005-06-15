/*
 * Created on 12.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ftp4che.results;

import java.util.List;

/**
 * @author arnold
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ResultImpl {
    List result;
    int resultValue;
    
    
	public void setResultLines(List lines) {
		this.result = lines;
	}

	public void setResultValue(int resultValue) {
		this.resultValue = resultValue;
	}

	public List getResultLines() {
		return this.result;
	}

	public int getResultValue() {
		return this.resultValue;
	}
	
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        for(int i = 0; i < result.size(); i++)
        {
           buffer.append(((String)result.get(i)).concat("\n"));
        }
        return buffer.toString();
    }

}
