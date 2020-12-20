package org.springframework.boot.netty.support;

import java.util.Map;

public class Error {

    /**
     * 是否有异常
     */
    protected boolean hasErrors;

    /**
     * 异常消息记录
     */
    protected Map<String, String> errorMsg;

    public boolean hasError() {
        return false;
    }
}
