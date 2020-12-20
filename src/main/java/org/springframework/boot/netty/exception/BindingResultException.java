package org.springframework.boot.netty.exception;

/**
 * Author: huoxingzhi
 * Date: 2020/12/11
 * Email: hxz_798561819@163.com
 */
public class BindingResultException extends RuntimeException {

    private String message;

    public BindingResultException() {
       super();
    }

    public BindingResultException(String message) {
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
