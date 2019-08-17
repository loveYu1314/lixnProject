package com.lixn.spring;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;

/**
 * @创建人 lixiangnan
 * @创建时间 2019/8/10
 * @描述
 */
public class ApplicationEventMulticasterDemo {

    public static void main(String[] args) {
        ApplicationEventMulticaster multicaster = new SimpleApplicationEventMulticaster();

        //添加监听器
        multicaster.addApplicationListener(event -> {
            System.err.println("接受到的事件为：" + event);
        });

        // 发布/广播事件
        multicaster.multicastEvent(new PayloadApplicationEvent<Object>("1","test1"));
        multicaster.multicastEvent(new PayloadApplicationEvent<Object>("2","test2"));
        multicaster.multicastEvent(new MyEvent("mytest"));
    }

    private static class MyEvent extends ApplicationEvent {

        public MyEvent(Object source) {
            super(source);
        }
    }
}
