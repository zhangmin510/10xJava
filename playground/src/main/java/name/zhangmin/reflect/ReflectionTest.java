package name.zhangmin.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author zhangmin.name
 * @date 2019/4/12
 */
public class ReflectionTest {

    public static void target(int i) {
        new Exception("#" + i).printStackTrace();
    }


    public static void main(String[] args) {
        Class<?> c = null;
        try {
            // 调用本地方法， 最好可以缓存起来
            c = Class.forName("name.zhangmin.reflect.ReflectionTest");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Method method = null;
        try {
            // 会遍历这个类所有的公有方法，如果没有匹配会遍历父类的方法
            // 非常耗时，最好缓存查询结果
            method = c.getMethod("target", int.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }


        try {
            ((Method) method).invoke(null, 0);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        // 一般反射只会执行一次， 所以JVM默认使用委派实现
        // 但是如果频繁调用，超过15次，就会使用动态实现
        try {
            for (int i = 0; i < 20; i++) {
                method.invoke(null, i);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
