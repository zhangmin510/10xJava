package name.zhangmin.designpattern.singleton;


/**
 * 这种实现方式比较常用，但是容易产生垃圾对象。这种实现方式通过ClassLoader机制
 * 类避免多线程的同步问题。
 * <p/>
 * 优点：不需要加锁，效率高；缺点：类加载时就进行初始化，浪费内存。
 * 
 * @author zhangmin
 * @date 2019/4/6
 */
public class SingletonV1 {
    private static SingletonV1 instance = new SingletonV1();

    private SingletonV1() {}

    public static SingletonV1 getInstance() {
        return instance;
    }
}
