package com.lixn.spring.servlet.v2;

import com.lixn.spring.annotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class MyDispatchServlet2 extends HttpServlet {

    //存储application.properties的配置内容
    private Properties contextConfig = new Properties();

    //存储所有扫描到的类
    private List<String> classNames = new ArrayList<String>();

    //IOC容器
    private Map<String, Object> iocBeanMap = new ConcurrentHashMap<String, Object>();

    //保存url和method的映射关系
    private Map<String, Method> handleMapping = new HashMap<String, Method>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        //6.调用，运行阶段 派遣分发任务
        //http://localhost/demo/query?name=lixn
        try {
            //委派模式
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("500 Exception , Detail : " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //绝对路径
        String url = req.getRequestURI();

        //处理成相对路径
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath, "").replaceAll("/+", "/");

        if (!this.handleMapping.containsKey(url)) {
            resp.getWriter().write("404 Not Found !!!");
            return;
        }
        Method method = this.handleMapping.get(url);
        //第一个参数：方法所在的实例
        //第二个参数：调用时所需要的实参
        Map<String, String[]> params = req.getParameterMap();
        //获取方法的形参列表
        Class<?>[] parameterTypes = method.getParameterTypes();
        //保存请求url的参数列表
        Map<String, String[]> parameterMap = req.getParameterMap();
        //保存赋值参数的位置
        Object[] paramValues = new Object[parameterTypes.length];
        //根据参数位置动态赋值
        for (int i = 0; i < parameterTypes.length; i++) {
            Class parameterType = parameterTypes[i];
            if (parameterType == HttpServletRequest.class) {
                paramValues[i] = req;
                continue;
            } else if (parameterType == HttpServletResponse.class) {
                paramValues[i] = resp;
                continue;
            } else if (parameterType == String.class) {
                //提取方法中加了注解的参数
                Annotation[][] annotations = method.getParameterAnnotations();
                for (int j = 0; j < annotations.length; j++) {
                    for (Annotation annotation : annotations[i]) {
                        if (annotation instanceof MyRequestParam) {
                            String paramName = ((MyRequestParam) annotation).value();
                            if (!"".equals(paramName.trim())) {
                                String value = Arrays.toString(parameterMap.get(paramName))
                                        .replaceAll("\\\\[|\\\\]", "")
                                        .replaceAll("\\s", ",");
                                paramValues[i] = value;
                            }
                        }
                    }
                }
            }
        }

        String beanName = lowerFirstCase(method.getDeclaringClass().getSimpleName());
        method.invoke(iocBeanMap.get(beanName), new Object[]{req, resp, params.get("name")[0]});
    }

    //初始化阶段
    @Override
    public void init(ServletConfig config) throws ServletException {
        //模板模式

        //1.加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));
        //2.扫描相关的类；
        doScanner(contextConfig.getProperty("scanPackage"));
        //3.初始化扫描到的类，将这些类放到IOC容器中 工厂模式实现
        doInstance();
        //4.完成依赖注入

        //在Spring中是通过调用getBean方法才出发依赖注入的
        doAutowired();
        //5.初始化HandleMapping
        initHandleMapping();
        System.out.println("my spring framework is inited !");
    }

    // 初始化url和Method的一对一对应关系
    private void initHandleMapping() {
        if (iocBeanMap.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : iocBeanMap.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(MyController.class)) {
                continue;
            }

            //获取Controller的url配置
            String baseUrl = "";
            if (clazz.isAnnotationPresent(MyRequestMapping.class)) {
                MyRequestMapping requestMapping = clazz.getAnnotation(MyRequestMapping.class);
                baseUrl = requestMapping.value();
            }
            //获取Method的url配置
            for (Method method : clazz.getMethods()) {
                //没有加RequestMapping注解的直接忽略
                if (!method.isAnnotationPresent(MyRequestMapping.class)) {
                    continue;
                }
                //映射URL
                MyRequestMapping requestMapping = method.getAnnotation(MyRequestMapping.class);
                String url = ("/" + baseUrl + "/" + requestMapping.value())
                        .replaceAll("/+", "/");
                handleMapping.put(url, method);
                System.out.println("Mapped URL : " + url + "," + method);
            }

        }
    }

    private void doAutowired() {
        if (iocBeanMap.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry : iocBeanMap.entrySet()) {
            //Declared 所有的字段，包括peivate/protected/default
            //普通的OOP编程只能拿到public的属性
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(MyAutowired.class)) {
                    continue;
                }
                MyAutowired autowired = field.getAnnotation(MyAutowired.class);
                //如果用户没有定义beanName，默认根据类型注入
                //这里省去对类名首字母小写的情况的判断
                String beanName = autowired.value().trim();
                if ("".equals(beanName)) {
                    //获得接口的类型，作为key，后面拿这个key去IOC容器中取值
                    beanName = field.getType().getName();
                }
                //设置私有属性的访问权限
                field.setAccessible(true);
                try {
                    // 用反射机制，动态给字段赋值
                    field.set(entry.getValue(), iocBeanMap.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }

    /**
     * 注册
     * 工厂模式实现
     */
    private void doInstance() {
        if (classNames.isEmpty()) {
            return;
        } else {
            try {
                for (String className : classNames) {
                    Class<?> clazz = Class.forName(className);
                    //在spring中用的多个子方法处理的
                    if (clazz.isAnnotationPresent(MyController.class)) {
                        Object instance = clazz.newInstance();
                        String beanName = lowerFirstCase(clazz.getSimpleName());
                        // 在Spring中在这个阶段不会put instance，这里put的是BeanDefinition
                        iocBeanMap.put(beanName, instance);
                    } else if (clazz.isAnnotationPresent(MyService.class)) {

                        //1.默认的类名首字母小写
                        String beanName = lowerFirstCase(clazz.getSimpleName());
                        //2.自定义命名
                        MyService service = clazz.getAnnotation(MyService.class);
                        //默认用类名首字母注入
                        //如果自己定义了beanName，那么优先使用自己定义的beanName
                        //如果是一个接口，使用接口的类型去自动注入
                        if (!"".equals(service.value())) {
                            beanName = service.value();
                        }
                        Object instance = clazz.newInstance();
                        iocBeanMap.put(beanName, instance);
                        Class<?>[] interfaces = clazz.getInterfaces();
                        for (Class<?> i : interfaces) {
                            //去除重复的bean
                            if (iocBeanMap.containsKey(i.getName())) {
                                throw new Exception("The " + i.getName() + " is existed !");
                            }
                            //把接口的类型当成key了
                            iocBeanMap.put(i.getName(), instance);
                        }
                    } else {
                        continue;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String lowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        //大小写字母的ASCII码相差32，而且大写字母的ASCII码比 小写字母的ASCII小
        // 在Java中，对char做算术运算，实际上就是对ASCII码做算术运算
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private void doScanner(String packageName) {
        URL url = this.getClass().getClassLoader().
                getResource("/" + packageName.replaceAll("\\.", "/"));
        File classDir = new File(url.getFile());
        for (File file : classDir.listFiles()) {
            if (file.isDirectory()) {
                doScanner(packageName + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String className = packageName + "." + file.getName()
                        .replace(".class", "");
                classNames.add(className);
            }
        }
    }

    private void doLoadConfig(String contextConfigLocation) {
        //在Spring中是通过Reader去查找和定位对不对
        InputStream fis = this.getClass().getClassLoader()
                .getResourceAsStream(contextConfigLocation.replace("classpath:", ""));
        try {
            contextConfig.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fis) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
