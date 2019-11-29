package com.lixn.spring.servlet.v3;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static jdk.nashorn.api.scripting.ScriptUtils.convert;

/**
 * @创建人 lixiangnan
 * @创建时间 2019/11/27
 * @描述
 */
public class MyDispatchServlet3 extends HttpServlet {

    private static final String LOCATION = "contextConfigLocation";

    private Properties properties = new Properties();

    private List<String> classNames = new ArrayList<String>();

    private Map<String, Object> iocBeanMap = new ConcurrentHashMap<String, Object>();

    //保存所有的Url和方法的映射关系
    private List<HandleMapping> handlerMapping = new ArrayList<HandleMapping>();

    public MyDispatchServlet3() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            doDispatch(req,resp); //开始始匹配到对应的方方法
        }catch(Exception e){
            //如果匹配过程出现异常，将异常信息打印出去
            resp.getWriter().write("500 Exception,Details:\r\n" + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", "").replaceAll(",\\s", "\r\n"));
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) {
        try {
            HandleMapping handleMapping = getHandleMapping(req);
            if (null == handleMapping) {
                //如果没有匹配上，返回404错误
                resp.getWriter().write("404 Not Found");
                return;
            }

            //获取方法的参数列表
            Class<?> [] paramTypes = handleMapping.method.getParameterTypes();

            //保存所有需要自动赋值的参数值
            Object [] paramValues = new Object[paramTypes.length];

            Map<String,String[]> params = req.getParameterMap();
            for (Map.Entry<String, String[]> param : params.entrySet()) {
                String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll(",\\s", ",");
                //如果找到匹配的对象，则开始填充参数值
                if(!handleMapping.paramIndexMapping.containsKey(param.getKey())){
                    continue;
                }
                int index = handleMapping.paramIndexMapping.get(param.getKey());
                paramValues[index] = convert(paramTypes[index],value);
            }

            //设置方法中的request和response对象
            int reqIndex = handleMapping.paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
            int respIndex = handleMapping.paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = resp;

            handleMapping.method.invoke(handleMapping.controller, paramValues);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //url传过来的参数都是String类型的，HTTP是基于字符串协议
    //只需要把String转换为任意类型就好
    private Object convert(Class<?> type,String value){
        if(Integer.class == type){
            return Integer.valueOf(value);
        }
        //如果还有double或者其他类型，继续加if
        //这时候，我们应该想到策略模式了
        //在这里暂时不实现，希望小伙伴自己来实现
        return value;
    }

    private HandleMapping getHandleMapping(HttpServletRequest req) {
        if(handlerMapping.isEmpty()){
            return null;
        }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath,"").replaceAll("/+","/");

        for(HandleMapping handle : handlerMapping){
            Matcher matcher = handle.pattern.matcher(url);
            //如果没有匹配上继续找下一个匹配
            if(!matcher.matches()){
                continue;
            }
            return handle;
        }

        return null;
    }

    //初始化，加载配置文件
    @Override
    public void init(ServletConfig config) throws ServletException {

        //模板模式

        //1.加载配置文件
        doLoadConfig(config.getInitParameter(LOCATION));

        //2.扫描所有相关类
        doScanner(properties.getProperty("scanPackage"));

        //3.初始化相关类的实例，并保存到IOC容器中
        doInstance();

        //4.依赖注入
        doAutowired();

        //5.构造HandleMapping
        initHandleMapping();

        //6.等待请求，匹配URL,定位方法，反射调用
        System.out.println("lixn mvcSpringframework is inited !!!");
    }

    private void initHandleMapping() {
        if(iocBeanMap.isEmpty()){
            return;
        }
        for (Map.Entry<String, Object> entry : iocBeanMap.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if(!clazz.isAnnotationPresent(MyController.class)){
                continue;
            }
            String url = "";
            //获取controller配置的url
            if(clazz.isAnnotationPresent(MyRequestMapping.class)){
                MyRequestMapping requestMapping = clazz.getAnnotation(MyRequestMapping.class);
                url = requestMapping.value();
            }
            //获取Method配置的url
            Method [] methods = clazz.getMethods();
            for(Method method : methods){
                //没有加RequestMapping注解的直接忽略
                if(!clazz.isAnnotationPresent(MyRequestMapping.class)){
                    continue;
                }
                //映射URL
                MyRequestMapping requestMapping = method.getAnnotation(MyRequestMapping.class);
                if(requestMapping == null){
                    continue;
                }
                String regex = ("/" + url + requestMapping.value()).replaceAll("/+","/");
                Pattern pattern = Pattern.compile(regex);
                handlerMapping.add(new HandleMapping(pattern,entry.getValue(),method));
                System.out.println("mapping " + regex + "," + method);
            }
        }

    }

    private void doAutowired() {
        if(iocBeanMap.isEmpty()){
            return;
        }
        for (Map.Entry<String, Object> entry : iocBeanMap.entrySet()) {
            //拿到实例对象中所有的属性
            Field [] fields = entry.getValue().getClass().getDeclaredFields();
            for(Field field : fields){
                if(!field.isAnnotationPresent(MyAutowired.class)){
                    continue;
                }
                MyAutowired autowired = field.getAnnotation(MyAutowired.class);
                String beanName = autowired.value().trim();
                if("".equals(beanName)){
                    beanName = field.getType().getName();
                }
                field.setAccessible(true);//设置私有属性的访问权限

                try {
                    field.set(entry.getValue(),iocBeanMap.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private void doInstance() {
        if (classNames.size() == 0) {
            return;
        }
        try {
            for (String className : classNames) {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(MyController.class)) {
                    //默认beanName的首字母为小写
                    String beanName = lowerFirst(clazz.getSimpleName());
                    iocBeanMap.put(beanName, clazz.newInstance());
                } else if (clazz.isAnnotationPresent(MyService.class)) {
                    MyService service = clazz.getAnnotation(MyService.class);
                    String beanName = service.value();
                    //如果用户设置了名字，就用设置的名字
                    if (!"".equals(beanName.trim())) {
                        iocBeanMap.put(beanName, clazz.newInstance());
                        continue;
                    }

                    //如果没有设置名字，就按接口类型创建一个实例
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> its : interfaces) {
                        iocBeanMap.put(its.getName(), clazz.newInstance());
                    }
                } else {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doScanner(String scanPackage) {
        // 将所有的保路经转换为文件路径
        URL url = this.getClass().getClassLoader()
                .getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                classNames.add(scanPackage + "." + file.getName().replace(".class", "").trim());
            }
        }
    }

    private void doLoadConfig(String initParameter) {
        InputStream inputStream = this.getClass().getClassLoader()
                .getResourceAsStream(initParameter.replace("classpath:", ""));

        try {
            //读取配置文件
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String lowerFirst(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    /**
     * 记录Controller中RequestMapping和Method的对应关系
     * 内部类
     */
    private class HandleMapping{

        private Pattern pattern;
        private Object controller;  //保存方法对应的实例
        private Method method;      //保存映射的方法
        private Map<String,Integer> paramIndexMapping;//参数顺序

        public HandleMapping(Pattern pattern, Object controller, Method method) {
            this.pattern = pattern;
            this.controller = controller;
            this.method = method;

            paramIndexMapping = new HashMap<String, Integer>();
            putParamIndexMapping(method);
        }

        private void putParamIndexMapping(Method method) {
            //提取方法中加了注解的参数
            Annotation [] [] annotations = method.getParameterAnnotations();
            for(int i = 0; i < annotations.length; i++){
                for(Annotation annotation: annotations[i]){
                    if(annotation instanceof MyRequestParam){
                        String paramName = ((MyRequestParam) annotation).value();
                        if(!"".equals(paramName.trim())){
                            paramIndexMapping.put(paramName,i);
                        }
                    }
                }
            }

            //提取方法中的request和response参数
            Class<?> [] paramTypes = method.getParameterTypes();
            for(int i = 0; i < paramTypes.length; i++){
                Class<?> paramType = paramTypes[i];
                if(paramType == HttpServletRequest.class || paramType == HttpServletResponse.class){
                    paramIndexMapping.put(paramType.getName(),i);
                }
            }
        }
    }
}
