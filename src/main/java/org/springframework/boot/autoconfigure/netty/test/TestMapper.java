package org.springframework.boot.autoconfigure.netty.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Author: huoxingzhi
 * Date: 2020/12/11
 * Email: hxz_798561819@163.com
 */
@Component
public class TestMapper {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void sayHello(){

        Map user = jdbcTemplate.queryForMap("SELECT * FROM user WHERE id=?", new Object[] {1});

        System.out.println(user);
    }

    public void sayUpdateNormal(int age){
        String sql = "update user set age = ? where id=?;";
        int update = jdbcTemplate.update(sql, age, 2);
        System.out.println("更新员工表,影响" + update + "行");
    }

    public void sayUpdateInNormal(int age){
        String sql = "update user set age = ? where id=?;";
        int update = jdbcTemplate.update(sql, age, 2);
        throw new RuntimeException("test exception");
    }
}
