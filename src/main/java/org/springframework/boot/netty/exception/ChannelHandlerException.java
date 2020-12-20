package org.springframework.boot.netty.exception;

public class ChannelHandlerException extends RuntimeException {

    private String message;

    public ChannelHandlerException() {
        super();
    }

    public ChannelHandlerException(String message) {
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
