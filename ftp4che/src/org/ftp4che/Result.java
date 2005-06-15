/*
 * Created on 11.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ftp4che;

import java.util.List;

/**
 * @author arnold
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface Result {
    public String toString();
    public void setResultLines(List lines);
    public void setResultValue(int resultValue);
    public List getResultLines();
    public int getResultValue();
}
