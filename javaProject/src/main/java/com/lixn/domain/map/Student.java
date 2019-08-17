package com.lixn.domain.map;

import java.util.Objects;

/**
 * @创建人 lixiangnan
 * @创建时间 2019/8/17
 * @描述
 */
public class Student implements Comparable<Student> {

    final String name;
    final int score;

    public Student(String name, int score) {
        this.name = name;
        this.score = score;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, score);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Student) {
            Student student = (Student) obj;
            return Objects.equals(this.name, student.name) && this.score == student.score;
        }
        return false;
    }

    @Override
    public int compareTo(Student o) {
        int n = Integer.compare(this.score,o.score);
        return n != 0 ? n : this.name.compareTo(o.name);
    }

//    @Override
//    public int compareTo(Student o) {
//        //排序没问题，treeMap根据key.compareTo(anther)==0判断是否相等，而不是equals()
//        return this.score < o.score ? -1 : 1;
//    }


}
