package org.springframework.boot.netty.listener;

import io.netty.channel.Channel;
import org.springframework.boot.netty.support.Error;

/**
 * Author: huoxingzhi
 * Date: 2020/12/10
 * Email: hxz_798561819@163.com
 */
public class Message<T> {

    private Channel currentChannel;

    private T content;

    private int index;

    private Error error;

    public Message(Channel currentChannel, T content, int index) {
        this.currentChannel = currentChannel;
        this.content = content;
        this.index = index;
    }

    public Message() {
    }

    public Channel getCurrentChannel() {
        return currentChannel;
    }

    public void setCurrentChannel(Channel currentChannel) {
        this.currentChannel = currentChannel;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "Message{" +
                "currentChannel=" + currentChannel +
                ", content='" + content + '\'' +
                ", index=" + index +
                '}';
    }
}
