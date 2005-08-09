package org.ftp4che.exception;

public class FtpIOException extends Exception {
    String code;
    String description;
    public FtpIOException(String code,String description)
    {
        super("FtpIOException --> Return Value: " + code + " Description: " + description);
        setCode(code);
        setDescription(description);
    }
    /**
     * @return Returns the code.
     */
    public String getCode() {
        return code;
    }
    /**
     * @param code The code to set.
     */
    public void setCode(String code) {
        this.code = code;
    }
    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
}
