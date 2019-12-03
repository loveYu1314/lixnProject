package com.lixn.lambda.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * @创建人 lixiangnan
 * @创建时间 2019/12/1
 * @描述
 */
public class Test3 {

    public static void main(String[] args) {
        List<String> list = Arrays.asList("hello","lixn","hello lixn");
        list.forEach(item -> System.out.println(item.toUpperCase()));

        System.out.println("1==============");
        List<String> list1 = new ArrayList<>();
        list.forEach(item -> list1.add(item.toUpperCase()));
        list1.forEach(item -> System.out.println(item.toUpperCase()));

        System.out.println("2=============");
        list.stream()
                .map(item -> item.toUpperCase())
                .forEach(item -> System.out.println(item));

        System.out.println("3==========");
        list.stream().map(String::toUpperCase).forEach(item -> System.out.println(item));

        System.out.println("4===============");
        //第一个参数是调用toUpperCase()方法的对象
        Function<String,String> function = String::toUpperCase;
        System.out.println(function.getClass().getInterfaces()[0]);
    }
}
