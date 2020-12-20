package org.springframework.boot.netty.exception;

public class ThreadException extends RuntimeException {

    private String message;

    public ThreadException() {
        super();
    }

    public ThreadException(String message) {
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
