package name.zhangmin.spring;

import name.zhangmin.spring.bean.CustomizedBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author zhangmin.name
 * @date 2019/4/12
 */
public class HelloSpring {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("application.xml");

        CustomizedBean customBean = context.getBean("customizedBean", CustomizedBean.class);
        customBean.hello();

        ((ClassPathXmlApplicationContext) context).registerShutdownHook();

        ((ClassPathXmlApplicationContext) context).stop();
    }
}
