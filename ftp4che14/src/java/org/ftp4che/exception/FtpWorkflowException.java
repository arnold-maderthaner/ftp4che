package org.ftp4che.exception;

public class FtpWorkflowException extends Exception {
    String code;
    String description;
    public FtpWorkflowException(String code,String description)
    {
        super("FtpWorkflowException --> Return Value: " + code + " Description: " + description);
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
