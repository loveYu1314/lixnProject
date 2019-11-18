package com.lixn.domain.thread;

/** @创建人 lixiangnan @创建时间 2019/10/11 @描述 */
public class ThreadDemo {

  private static int count = 0;

  public static void inc() {
    try {
      Thread.sleep(1);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    count++;
  }

  public static void main(String[] args) throws InterruptedException {
    for (int i = 0; i < 1000; i++) {
      new Thread(() -> ThreadDemo.inc()).start();
    }
    Thread.sleep(30);
    System.out.println("运行结果：" + count);
  }
}
