package org.ftp4che.exception;

import java.net.ConnectException;

public class ProxyConnectionException extends ConnectException {

    private int status = 0;

    private String msg = "";

    public ProxyConnectionException() {
        super();
    }

    public ProxyConnectionException(String msg) {
        super(msg);
        setMsg(msg);
    }

    public ProxyConnectionException(int status, String msg) {
        this(msg);
        setStatus(status);
    }

    /**
     * @return Returns the msg.
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg
     *            The msg to set.
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * @return Returns the status.
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status
     *            The status to set.
     */
    public void setStatus(int status) {
        this.status = status;
    }

}
