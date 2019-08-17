package com.lixn.spring;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @创建人 lixiangnan
 * @创建时间 2019/8/10
 * @描述
 */
public class SpringEventListenerDemo {

    public static void main(String[] args) {
        GenericApplicationContext context = new GenericApplicationContext();
        //添加事件监听器
//        context.addApplicationListener(new ApplicationListener<ApplicationEvent>() {
//            @Override
//            public void onApplicationEvent(ApplicationEvent event) {
//                System.err.println("监听事件：" + event);
//            }
//        });

        //添加自定义监听器
        context.addApplicationListener(new ClosedListener());
        context.addApplicationListener(new RefreshedListener());

        // 启动spring上下文
        context.refresh();

        //spring应用上下文发布事件
        // ContextRefreshedEvent 和 PayloadApplicationEvent
        context.publishEvent("test");

        context.publishEvent(new MyEvent("myTest"));

        // 关闭应用上下文 ContextClosedEvent
        context.close();
    }

    private static class ClosedListener implements ApplicationListener<ContextClosedEvent> {

        @Override
        public void onApplicationEvent(ContextClosedEvent event) {
            System.err.println("关闭上下文：" + event);
        }
    }

    private static class RefreshedListener implements ApplicationListener<ContextRefreshedEvent> {

        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            System.err.println("上下文启动：" + event);
        }
    }

    private static class MyEvent extends ApplicationEvent {

        public MyEvent(Object source) {
            super(source);
        }
    }
}
