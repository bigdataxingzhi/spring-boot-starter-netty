package org.springframework.boot.autoconfigure.netty.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.netty.channel.Channel;
import org.springframework.boot.autoconfigure.netty.support.ScanSupport;
import org.springframework.boot.netty.annotation.NettySockerHandler;
import org.springframework.boot.netty.annotation.NettySocketListener;
import org.springframework.boot.netty.annotation.SocketBody;
import org.springframework.boot.netty.annotation.Validation;
import org.springframework.boot.netty.support.BindingResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: huoxingzhi
 * Date: 2020/12/11
 * Email: hxz_798561819@163.com
 */

@Service
@NettySocketListener
public class TestService {

    @Autowired
    TestMapper testMapper;

    @Autowired
    private ScanSupport scanSupport;



    @NettySockerHandler
    @Transactional
    public void testHandler(Channel channel, @SocketBody @Validation(groups = {TestGroup.class}) User text, BindingResult bindingResult){
        Map<String,Class<?>> cachedProtoBufBeanClass = new HashMap<>();
        System.out.println("你好！I am NettySockerHandler : receive "+text.getName()+" "+bindingResult.getMessage());
        testMapper.sayHello();
        testMapper.sayUpdateNormal(50);
        testMapper.sayHello();
    }
}
