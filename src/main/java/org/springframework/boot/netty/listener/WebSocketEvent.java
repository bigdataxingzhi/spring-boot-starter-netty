package org.springframework.boot.netty.listener;

import org.springframework.boot.netty.enums.MessageEnum;
import org.springframework.context.ApplicationEvent;

/**
 * Author: huoxingzhi
 * Date: 2020/12/10
 * Email: hxz_798561819@163.com
 */
public class WebSocketEvent extends ApplicationEvent {

    private MessageEnum message;

    private int mapIndex = -1;


    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public WebSocketEvent(Object source) {
        super(source);
        this.message = (MessageEnum)source;
    }

    public int getMapIndex() {
        return mapIndex;
    }

    public void setMapIndex(int mapIndex) {
        this.mapIndex = mapIndex;
    }

    public MessageEnum getMessage() {
        return message;
    }

    public void setMessage(MessageEnum message) {
        this.message = message;
    }
}
