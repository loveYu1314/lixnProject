package com.lixn.spring.servlet.v1;

import com.lixn.spring.annotation.MyAutowired;
import com.lixn.spring.annotation.MyController;
import com.lixn.spring.annotation.MyRequestMapping;
import com.lixn.spring.annotation.MyService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class MyDispatchServlet extends HttpServlet {

    private Properties contextConfig = new Properties();

    private List<String> classNames = new ArrayList<String>();

    //IOC容器
    private Map<String, Object> beanMap = new ConcurrentHashMap<String,Object>();

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
        //6.调用，运行阶段
        //http://localhost/demo/query?name=lixn
        try {
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
        url = url.replaceAll(contextPath,"").replaceAll("/+","/");

        if(!this.handleMapping.containsKey(url)){
            resp.getWriter().write("404 Not Found !!!");
            return;
        }
        Method method = this.handleMapping.get(url);
        //投机取巧
        //通过反射拿到method所在class，拿到class之后还是拿到class的名称
        //再调用lowerFirstCase拿到beanName
        String beanName = lowerFirstCase(method.getDeclaringClass().getSimpleName());

        //投机取巧，暂时写死
        Map<String,String[]> paraMap = req.getParameterMap();
        method.invoke(beanMap.get(beanName),new Object[]{req,resp,paraMap.get("name")[0]});
    }

    //初始化阶段
    @Override
    public void init(ServletConfig config) throws ServletException {

        //1.加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));
        //2.扫描相关的类；
        doScanner(contextConfig.getProperty("scanPackage"));
        //3.初始化扫描到的类，将这些类放到IOC容器中
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
        if (beanMap.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(MyController.class)) {
                continue;
            }

            //保存写在类上的@MyRequestMapping（"/test"）
            String baseUrl = "";
            if (clazz.isAnnotationPresent(MyRequestMapping.class)) {
                MyRequestMapping requestMapping = clazz.getAnnotation(MyRequestMapping.class);
                baseUrl = requestMapping.value();
            }
            //默认获取所有的public方法
            for (Method method : clazz.getMethods()) {
                if (!method.isAnnotationPresent(MyRequestMapping.class)) {
                    continue;
                }
                MyRequestMapping requestMapping = method.getAnnotation(MyRequestMapping.class);
                String url = ("/"+baseUrl + "/" + requestMapping.value())
                        .replaceAll("/+", "/");
                handleMapping.put(url, method);
                System.out.println("Mapped : " + url + "," + method);
            }

        }
    }

    private void doAutowired() {
        if (beanMap.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
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
                field.setAccessible(true);
                try {
                    // 用反射机制，动态gei字段赋值
                    field.set(entry.getValue(), beanMap.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 注册
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
                        String beanName = lowerFirstCase(clazz.getSimpleName());
                        // 在Spring中在这个阶段不会put instance，这里put的是BeanDefinition
                        beanMap.put(beanName, clazz.newInstance());
                    } else if (clazz.isAnnotationPresent(MyService.class)) {
                        MyService service = clazz.getAnnotation(MyService.class);
                        //默认用类名首字母注入
                        //如果自己定义了beanName，那么优先使用自己定义的beanName
                        //如果是一个接口，使用接口的类型去自动注入
                        String beanName = service.value();
                        if ("".equals(beanName.trim())) {
                            beanName = lowerFirstCase(clazz.getSimpleName());
                        }
                        Object instance = clazz.newInstance();
                        beanMap.put(beanName, instance);
                        Class<?>[] interfaces = clazz.getInterfaces();
                        for (Class<?> i : interfaces) {
                            //去除重复的bean
                            if (beanMap.containsKey(i.getName())) {
                                throw new Exception("The " + i.getName() + " is existed !");
                            }
                            //把接口的类型当成key了
                            beanMap.put(i.getName(), instance);
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
                classNames.add(packageName + "." + file.getName()
                        .replace(".class", ""));
            }
        }
    }

    private void doLoadConfig(String contextConfigLocation) {
        //在Spring中是通过Reader去查找和定位对不对
        InputStream fis = this.getClass().getClassLoader()
                .getResourceAsStream(contextConfigLocation.replace("classpath:",""));
        try {
            contextConfig.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != fis){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
