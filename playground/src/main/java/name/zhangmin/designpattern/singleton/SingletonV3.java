package name.zhangmin.designpattern.singleton;

/**
 * 只适用于静态域的情况。使用了类加载机制来保证初始化时只有一个线程。
 *
 * @author zhangmin
 * @date 2019/4/6
 */
public class SingletonV3 {
    private static class SingletonHolder {
        private static final SingletonV3 INSTANCE = new SingletonV3();
    }

    private SingletonV3() {}

    public static final SingletonV3 getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
