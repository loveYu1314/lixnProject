package com.lixn.domain.map;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @创建人 lixiangnan
 * @创建时间 2019/8/17
 * @描述
 */
public class MapDemoMain {
    public static void main(String[] args) {
        Map<Student,Integer> hashMap = new HashMap<>();
        hashMap.put(new Student("lixn",99),99);
        System.out.println(hashMap.get(new Student("lixn",99)));

        Map<Student,Integer> treeMap = new TreeMap<>();
        treeMap.put(new Student("666",88),88);
        System.out.println(treeMap.get(new Student("666",88)));
    }
}
