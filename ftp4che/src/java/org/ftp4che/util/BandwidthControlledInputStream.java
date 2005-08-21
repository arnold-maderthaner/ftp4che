/**                                                                         *
 *  This file is part of ftp4che.                                            *
 *                                                                           *
 *  This library is free software; you can redistribute it and/or modify it  *
 *  under the terms of the GNU General Public License as published           *
 *  by the Free Software Foundation; either version 2 of the License, or     *
 *  (at your option) any later version.                                      *
 *                                                                           *
 *  This library is distributed in the hope that it will be useful, but      *
 *  WITHOUT ANY WARRANTY; without even the implied warranty of               *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU        *
 *  General Public License for more details.                                 *
 *                                                                           *
 *  You should have received a copy of the GNU General Public                *
 *  License along with this library; if not, write to the Free Software      *
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA  *
 *                                                                           *
 *****************************************************************************/
package org.ftp4che.util;

import java.io.IOException;
import java.io.InputStream;

public class BandwidthControlledInputStream extends InputStream {

    private InputStream in;
    private static long MAXIMUM_TIME = 1000;
    private int maximumBytes;
    private int bytesRead;
    private long startTime = System.currentTimeMillis();

    public BandwidthControlledInputStream(InputStream in, int maximumBytes) {
        this.in = in;
        this.maximumBytes = maximumBytes;
    }
    
    public int available() {
        long readTime = System.currentTimeMillis() - startTime;
        if (readTime > MAXIMUM_TIME) resetStartTime();

        return ((maximumBytes - bytesRead) < 0 ? 0 : maximumBytes - bytesRead);
    }

    public boolean markSupported() {
        return in.markSupported();
    }

    public void mark(int readlimit) {
        in.mark(readlimit);
    }

    public long skip(long n) throws IOException {
        return in.skip(n);
    }
    
    public void reset() throws IOException {
        in.reset();
    }
    
    public void close() throws IOException {
        in.close();
        in = null;
    }

    public int read() throws IOException {
        int b = in.read();
        waitBytes(1);
        bytesRead++;
        return b;
    }

    public int read(byte b[]) throws IOException {
        int r = in.read(b);
        if (r != -1)
            waitBytes(r);
        bytesRead += r;
        return r;
    }

    public int read(byte b[], int off, int len) throws IOException {
        int r = in.read(b, off, len);
        if (r != -1)
            waitBytes(r);
        bytesRead += r;
        return r;
    }

    private void resetStartTime() {
        startTime = System.currentTimeMillis();
        bytesRead = 0;
    }
    
    private int getReadDuration(int byteCount) {
        long remaining = 0;
        int waitingCount = byteCount - available();
        
        if (waitingCount < 0) waitingCount = 0;
        else {
            remaining = MAXIMUM_TIME - (System.currentTimeMillis() - startTime);
            remaining = ( remaining < 0 ? 0 : remaining );
            waitingCount = ( (waitingCount - maximumBytes) < 0 ? 0 : waitingCount - maximumBytes );
        }
        
        return (int) (MAXIMUM_TIME * waitingCount / maximumBytes + remaining);
    }

    private void waitBytes(int byteCount) {
        long endTime = System.currentTimeMillis() + getReadDuration(byteCount);
        
        while (endTime > System.currentTimeMillis()) {
            try {
                Thread.sleep(endTime - System.currentTimeMillis());
            } catch (Exception e) {
            }
        }
    }
}