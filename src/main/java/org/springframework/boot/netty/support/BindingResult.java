package org.springframework.boot.netty.support;

import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Map;


public class BindingResult extends Error{


    /**
     * 获取异常消息组装
     *
     * @return
     */
    public String getMessage() {
        if (errorMsg == null || errorMsg.isEmpty()) {
            return StringUtils.EMPTY;
        }
        StringBuilder message = new StringBuilder();
        errorMsg.forEach((key, value) -> {
            message.append(MessageFormat.format("{0}:{1} \r\n", key, value));
        });
        return message.toString();
    }


    private boolean isHasErrors() {
        return hasErrors;
    }

    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    @Override
    public boolean hasError() {
        return isHasErrors();
    }

    public Map<String, String> getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(Map<String, String> errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "BindingResult{" +
                "hasErrors=" + hasErrors +
                ", errorMsg=" + errorMsg +
                '}';
    }
}