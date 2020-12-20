package org.springframework.boot.autoconfigure.netty;

import io.netty.util.NettyRuntime;
import io.netty.util.internal.SystemPropertyUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.netty.enums.StartUpMode;
import org.springframework.boot.autoconfigure.netty.support.RegisterArguemnetResolver;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Author: huoxingzhi
 * Date: 2020/12/10
 * Email: hxz_798561819@163.com
 */

@ConfigurationProperties(prefix = "spring.netty")
public class NettyProperties {

    private int serverPort = 8888;

    private int messageMapCapacity = SystemPropertyUtil.getInt(
            "io.netty.eventLoopThreads",
            NettyRuntime.availableProcessors() * 2);

    private int bossGroupLoopThreads = 1;

    private int workGroupLoopThreads = SystemPropertyUtil.getInt(
            "io.netty.eventLoopThreads",
            NettyRuntime.availableProcessors() * 2);

    private int messageQueueCapatity = 1000;

    private long consumerThreadSleep = 500L;

    private Long messageQueueTimeout = 500L;

    private int consumerProcessThreadCorePoolSize = 10;

    private int consumerProcessThreadPoolMaxPoolSize = 100;

    private int consumerProcessThreadPoolQueueCapacity = 10;

    private String protoBufBasePackage = "";

    private StartUpMode startUpMode = StartUpMode.SERVER;

    private String serverIpAddress = "localhost";

    private int maxFramePayloadLength = 1024*64;

    private boolean useWebSocketCompression = false;

    private String[] arguementResolverBasePackages = new String[]{};

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getMessageMapCapacity() {
        return messageMapCapacity;
    }

    public void setMessageMapCapacity(int messageMapCapacity) {
        this.messageMapCapacity = messageMapCapacity;
    }

    public int getBossGroupLoopThreads() {
        return bossGroupLoopThreads;
    }

    public void setBossGroupLoopThreads(int bossGroupLoopThreads) {
        this.bossGroupLoopThreads = bossGroupLoopThreads;
    }

    public int getWorkGroupLoopThreads() {
        return workGroupLoopThreads;
    }

    public void setWorkGroupLoopThreads(int workGroupLoopThreads) {
        this.workGroupLoopThreads = workGroupLoopThreads;
    }

    public int getMessageQueueCapatity() {
        return messageQueueCapatity;
    }

    public void setMessageQueueCapatity(int messageQueueCapatity) {
        this.messageQueueCapatity = messageQueueCapatity;
    }

    public long getConsumerThreadSleep() {
        return consumerThreadSleep;
    }

    public void setConsumerThreadSleep(long consumerThreadSleep) {
        this.consumerThreadSleep = consumerThreadSleep;
    }

    public Long getMessageQueueTimeout() {
        return messageQueueTimeout;
    }

    public void setMessageQueueTimeout(Long messageQueueTimeout) {
        this.messageQueueTimeout = messageQueueTimeout;
    }

    public int getConsumerProcessThreadCorePoolSize() {
        return consumerProcessThreadCorePoolSize;
    }

    public void setConsumerProcessThreadCorePoolSize(int consumerProcessThreadCorePoolSize) {
        this.consumerProcessThreadCorePoolSize = consumerProcessThreadCorePoolSize;
    }

    public int getConsumerProcessThreadPoolMaxPoolSize() {
        return consumerProcessThreadPoolMaxPoolSize;
    }

    public void setConsumerProcessThreadPoolMaxPoolSize(int consumerProcessThreadPoolMaxPoolSize) {
        this.consumerProcessThreadPoolMaxPoolSize = consumerProcessThreadPoolMaxPoolSize;
    }

    public int getConsumerProcessThreadPoolQueueCapacity() {
        return consumerProcessThreadPoolQueueCapacity;
    }

    public void setConsumerProcessThreadPoolQueueCapacity(int consumerProcessThreadPoolQueueCapacity) {
        this.consumerProcessThreadPoolQueueCapacity = consumerProcessThreadPoolQueueCapacity;
    }

    public String getProtoBufBasePackage() {
        return protoBufBasePackage;
    }

    public void setProtoBufBasePackage(String protoBufBasePackage) {
        this.protoBufBasePackage = protoBufBasePackage;
    }

    public StartUpMode getStartUpMode() {
        return startUpMode;
    }

    public void setStartUpMode(StartUpMode startUpMode) {
        this.startUpMode = startUpMode;
    }

    public String getServerIpAddress() {
        return serverIpAddress;
    }

    public void setServerIpAddress(String serverIpAddress) {
        this.serverIpAddress = serverIpAddress;
    }

    public int getMaxFramePayloadLength() {
        return maxFramePayloadLength;
    }

    public void setMaxFramePayloadLength(int maxFramePayloadLength) {
        this.maxFramePayloadLength = maxFramePayloadLength;
    }

    public boolean isUseWebSocketCompression() {
        return useWebSocketCompression;
    }

    public void setUseWebSocketCompression(boolean useWebSocketCompression) {
        this.useWebSocketCompression = useWebSocketCompression;
    }

    public String[] getArguementResolverBasePackages() {
        return arguementResolverBasePackages;
    }

    public void setArguementResolverBasePackages(String[] arguementResolverBasePackages) {
        this.arguementResolverBasePackages = arguementResolverBasePackages;
    }

}
