package com.lixn.lambda.demo;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @创建人 lixiangnan
 * @创建时间 2019/11/10
 * @描述
 */
public class Test1 {
    public static void main(String[] args) {
        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        Objects.requireNonNull(integers);
        //基本遍历 外部迭代
        for(int i = 0;i < integers.size();i++){
            System.out.println(integers.get(i));
        }
        System.out.println("===基本遍历结束===");

        // 外部迭代
        for(Integer i : integers){
            System.out.println(i);
        }
        System.out.println("===增强for循环结束===");

        // 内部迭代
        integers.forEach(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                System.out.println(integer);
            }
        });
        System.out.println("===java8遍历结束===");

        integers.forEach(i -> {
            System.out.println(i * 2);
        });
        System.out.println("===Lambda 遍历结束===");

        // 方法引用（method references）
        integers.forEach(System.out::println);
        System.out.println("===方法引用 遍历结束===");
    }
}
