package org.springframework.boot.netty.exception;

/**
 * Author: huoxingzhi
 * Date: 2020/12/11
 * Email: hxz_798561819@163.com
 */
public class NoHandlerException extends RuntimeException {

    private String message;

    public NoHandlerException() {
        super();
    }

    public NoHandlerException(String message) {
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
