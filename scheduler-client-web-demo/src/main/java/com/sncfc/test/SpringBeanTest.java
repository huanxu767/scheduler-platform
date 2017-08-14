package com.sncfc.test;

import org.springframework.stereotype.Component;

/**
 * Created by 123 on 2017/1/18.
 */
@Component
public class SpringBeanTest {

    public void test(){
        System.out.println("测试");
    }

    public void test(String a,String b){
        System.out.println("测试"+a+b);
    }
}
