package org.springframework.boot.autoconfigure.netty;


import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.netty.channelInitializer.AbstractChannelInitializer;
import org.springframework.boot.autoconfigure.netty.server.NettySocketServer;
import org.springframework.boot.autoconfigure.netty.support.RegisterArguemnetResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.netty.argumentResolver.ChannelAwareMethodArgumentResolver;
import org.springframework.boot.netty.argumentResolver.ErrorMessageMethodArgumentResolver;
import org.springframework.boot.netty.argumentResolver.HandlerMethodArgumentResolver;
import org.springframework.boot.netty.argumentResolver.NettyPayloadMethodProcessor;
import org.springframework.boot.netty.converter.ProtobufMessage2JsonConverter;
import org.springframework.boot.netty.service.DefaultUserDetailService;
import org.springframework.boot.netty.service.UserDetailService;
import org.springframework.boot.netty.converter.JsonMessageConverter;
import org.springframework.boot.netty.converter.MessageConverter;
import org.springframework.boot.netty.handler.HearBeatHandlerFactory;
import org.springframework.boot.netty.handler.common.NettyCustomerizerChannelInboundHandlerAdapterFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author: huoxingzhi
 * Date: 2020/12/10
 * Email: hxz_798561819@163.com
 */
@Configuration
@ConditionalOnClass({NioEventLoopGroup.class, NioEventLoop.class})
@EnableConfigurationProperties(NettyProperties.class)
public class NettyAutoConfiguration implements ApplicationContextAware, InitializingBean {

    @Autowired
    NettyProperties nettyProperties;

    BeanDefinitionRegistry beanDefinitonRegistry;

    @Bean
    NettySocketServer webSocketServer(NettyProperties nettyProperties){

        return new NettySocketServer(nettyProperties);
    }

   @Bean
   NettyStartupListener nettyStartupListener(NettySocketServer nettySocketServer, AbstractChannelInitializer channelInitializer){

       return new NettyStartupListener(nettySocketServer,channelInitializer);
   }


    @Bean("jsonMessageConverter")
    MessageConverter jsonMessageConverter(){

        return  new JsonMessageConverter();
    }

    @Bean("protoBufMessageConverter")
    MessageConverter protoBufMessageConverter(){

        return  new ProtobufMessage2JsonConverter();
    }

    @Bean
    NettyCustomerizerChannelInboundHandlerAdapterFactory heartBeatHandler (){

        return new HearBeatHandlerFactory();
    }

    @Bean
    HandlerMethodArgumentResolver channelAwareMethodArgumentResolver(){

        return new ChannelAwareMethodArgumentResolver();
    }

    @Bean
    HandlerMethodArgumentResolver nettyPayloadMethodProcessor(){

        return new NettyPayloadMethodProcessor();
    }


    @Bean
    ErrorMessageMethodArgumentResolver errorMessageMethodArgumentResolver(){

        return new ErrorMessageMethodArgumentResolver();
    }

    @Bean
    @ConditionalOnMissingBean(UserDetailService.class)
    UserDetailService defaultUserDetailService(){

        return new DefaultUserDetailService();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        RegisterArguemnetResolver.doRegisterArguements(this.beanDefinitonRegistry,this.nettyProperties.getArguementResolverBasePackages());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        this.beanDefinitonRegistry = (BeanDefinitionRegistry) configurableApplicationContext
                .getBeanFactory();
    }
}
