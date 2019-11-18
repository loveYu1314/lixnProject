package com.lixn.lambda.demo;

/**
 * @创建人 lixiangnan
 * @创建时间 2019/11/10
 * @描述
 */
@FunctionalInterface
interface FunctionInterfaceTest {

    void test();

    String toString();
}

class Test2{

    public void test2(FunctionInterfaceTest test){
        System.out.println(1);
        test.test();
        System.out.println(2);
    }

    public static void main(String[] args) {
        Test2 test2 = new Test2();
        test2.test2(new FunctionInterfaceTest() {
            @Override
            public void test() {
                System.out.println("===test===");
            }
        });

        test2.test2(() -> {
            System.out.println("===Lambda test===");
        });

        // 相当于实现了FunctionInterfaceTest接口
        // 在Java中，Lambda表达式是对象不是函数 class com.lixn.lambda.demo.Test2$$Lambda$2/1831932724
        FunctionInterfaceTest interfaceTest = () -> {
            System.out.println("===hello 666! 实现了FunctionInterfaceTest接口===");
        };

        System.out.println(interfaceTest.getClass());
        System.out.println(interfaceTest.getClass().getSuperclass());
        System.out.println(interfaceTest.getClass().getInterfaces().length);
        System.out.println(interfaceTest.getClass().getInterfaces()[0]);
    }
}
