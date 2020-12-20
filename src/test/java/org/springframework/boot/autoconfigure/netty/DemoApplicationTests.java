package org.springframework.boot.autoconfigure.netty;

import com.google.protobuf.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.netty.support.ScanSupport;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private ScanSupport scanSupport;

    @Test
    void contextLoads() {
        Map<String,Class<?>> cachedProtoBufBeanClass = new HashMap<>();
        try {
            scanSupport
                    .doScan("org.springframework.boot.autoconfigure.netty.testProtoBuf")
                    .stream().filter(
                    protoBufBeanClass -> {
                        boolean filtered = Message.class.isAssignableFrom(protoBufBeanClass);
                        return filtered;
                    }
            ).forEach(protoBufBeanClass -> {
                cachedProtoBufBeanClass.put(protoBufBeanClass.getSimpleName(),protoBufBeanClass);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("你好！I am NettySockerHandler : receive "+" "+cachedProtoBufBeanClass);
    }

}
