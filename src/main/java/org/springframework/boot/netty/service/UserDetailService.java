package org.springframework.boot.netty.service;

import java.util.UUID;

/**
 * Author: huoxingzhi
 * Date: 2020/12/11
 * Email: hxz_798561819@163.com
 */
public interface UserDetailService {

    default String getUserClientId(Object object){

        return UUID.randomUUID().toString();
    }
}
