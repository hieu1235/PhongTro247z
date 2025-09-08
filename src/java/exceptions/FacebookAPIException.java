package exceptions;

public class FacebookAPIException extends Exception {
    private String errorCode;
    private String errorType;
    
    public FacebookAPIException(String message) {
        super(message);
    }
    
    public FacebookAPIException(String message, String errorCode, String errorType) {
        super(message);
        this.errorCode = errorCode;
        this.errorType = errorType;
    }
    
    public FacebookAPIException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getErrorType() {
        return errorType;
    }
}