package com.lixn.domain.thread;

/**
 * @创建人 lixiangnan
 * @创建时间 2019/10/11
 * @描述
 */
public class SynchronizedDemo {

    private static int count = 0;

    public static void inc() {
        synchronized (SynchronizedDemo.class) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 1000; i++) {
            new Thread(() -> SynchronizedDemo.inc()).start();
        }
        Thread.sleep(3000);
        System.out.println("运行结果:" + count);
    }
}
