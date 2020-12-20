package org.springframework.boot.netty.exception;

public class ValidatorException extends RuntimeException {

    private String message;

    public ValidatorException() {
        super();
    }

    public ValidatorException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
