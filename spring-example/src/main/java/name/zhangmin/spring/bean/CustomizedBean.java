package name.zhangmin.spring.bean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.Lifecycle;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author zhangmin.name
 * @date 2019/4/12
 */
public class CustomizedBean implements DisposableBean, InitializingBean,
        Lifecycle, ApplicationContextAware {
    private boolean isRunning = false;

    // CommonAnnotationBeanPostProcessor这个BeanPostProcessor通过继承
    // InitDestroyAnnotationBeanPostProcessor对@javax.annotation.PostConstruct
    // 和@javax.annotation.PreDestroy注解的支持
    @PostConstruct
    public void init() {
        System.out.println("PostConstruct init");
    }

    @PreDestroy
    public void dispose() {
        System.out.println("PreDestroy dispose");
    }

    public void hello() {
        System.out.println("hello bean");
    }

    // DisposableBean
    @Override
    public void destroy() throws Exception {
        System.out.println("DisposableBean destroy");
    }

    // InitializingBean
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("InitializingBean afterPropertiesSet");
    }

    // Lifecycle
    @Override
    public void start() {
        System.out.println("Demo bean start");
        isRunning = true;
    }

    @Override
    public void stop() {
        System.out.println("Demo bean start");
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("ApplicationContextAware: " + applicationContext.getDisplayName());
    }
}
