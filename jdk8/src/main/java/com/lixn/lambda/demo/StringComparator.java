package com.lixn.lambda.demo;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @创建人 lixiangnan
 * @创建时间 2019/12/1
 * @描述
 */
public class StringComparator {

    public static void main(String[] args) {
        List<String> list = Arrays.asList("tom","mic","san");
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o2.compareTo(o1);
            }
        });

        System.out.println("1=================");
        System.out.println(list);

        // Collections.sort(list, Comparator.reverseOrder());
        //  Collections.sort(list,(o1,o2) -> o2.compareTo(o1));
        Collections.sort(list,(String o1,String o2) -> {
            return o2.compareTo(o1);
        });
        System.out.println("2=================");
        System.out.println(list);
    }
}
