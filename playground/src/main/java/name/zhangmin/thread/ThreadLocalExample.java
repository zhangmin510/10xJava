package name.zhangmin.thread;

import java.lang.management.ThreadMXBean;

/**
 * ThreadLocal使用示例。
 *
 * ThreadLocal创建的变量只能被当前线程访问，其他线程则无法访问和修改
 *
 * @author zhangmin.name
 * @date 2019/4/10
 */
public class ThreadLocalExample extends Thread {
    ThreadLocal<String> tl = new ThreadLocal<String>() {
        // 设置初始值
        @Override
        protected String initialValue() {
            return Thread.currentThread().getName();
        }
    };

    @Override
    public void run() {
        super.run();
        System.out.println(tl.get());

        // 子线程也可以访问
        final ThreadLocal<String> threadLocal = new InheritableThreadLocal<String>();
        threadLocal.set(tl.get());
        new Thread(new Runnable() {
            public void run() {
                System.out.println("children thread get: " + threadLocal.get());
            }
        }).start();
    }

    public static void main(String[] args) {
        ThreadLocalExample tle = new ThreadLocalExample();
        tle.start();
    }
}
