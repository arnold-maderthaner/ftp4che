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
import java.io.OutputStream;

public class BandwidthControlledOutputStream extends OutputStream {
    
    private OutputStream out;    
    private static long MAXIMUM_TIME = 1000;
    private int maximumBytes;
    private int bytesWritten;
    private long startTime = System.currentTimeMillis();

    public BandwidthControlledOutputStream(OutputStream out, int maximumBytes) {
        this.out = out;
        this.maximumBytes = maximumBytes;
    }

    private int available() {
        long writeTime = System.currentTimeMillis() - startTime;
        if (writeTime >= MAXIMUM_TIME) resetStartTime();

        return ((maximumBytes - bytesWritten) < 0 ? 0 : maximumBytes - bytesWritten);
    }
    
    public void write(int b) throws IOException {
        waitBytes(1);
        bytesWritten++;
        out.write(b);
    }

    public void write(byte b[]) throws IOException {
        write(b, 0, b.length);
    }

    public void write(byte b[], int off, int len) throws IOException {
        int avail = available();
        if (avail >= len) {
            bytesWritten += len;
            out.write(b, off, len);
        } else {
            int offset = 0;
            while (offset < len) {
                bytesWritten += avail;
                out.write(b, off + offset, avail);
                offset += avail;
                waitBytes(maximumBytes);
                avail = available();
                if (avail > len - offset)
                    avail = len - offset;
            }
        }
    }
    
    private void resetStartTime() {
        startTime = System.currentTimeMillis();
        bytesWritten = 0;
    }

    private int getWriteDuration(int no_bytes) {
        long remain = 0;
        int bytesToWaitFor = no_bytes - available();
        if (bytesToWaitFor < 0) {
            bytesToWaitFor = 0;
        } else {
            remain = MAXIMUM_TIME - (System.currentTimeMillis() - startTime);
            if (remain < 0)
                remain = 0;
            bytesToWaitFor -= maximumBytes;
            if (bytesToWaitFor < 0)
                bytesToWaitFor = 0;
        }
        return (int) (MAXIMUM_TIME * bytesToWaitFor / maximumBytes + remain);
    }
    
    private void waitBytes(int bytes) {
        long endTime = System.currentTimeMillis() + getWriteDuration(bytes);
        
        while (endTime > System.currentTimeMillis()) {
            try {
                Thread.sleep(endTime - System.currentTimeMillis());
            } catch (Exception e) {
            }
        }
    }
}