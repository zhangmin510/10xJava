package name.zhangmin.designpattern.singleton;

/**
 * 双重锁检查线程安全，懒加载。
 *
 * @author zhangmin
 * @date 2019/4/6
 */
public class SingletonV2 {
    private volatile static SingletonV2 instance;

    private SingletonV2() {}

    public static SingletonV2 getInstance() {
        SingletonV2 tmp = instance; //减少主存和线程内存的拷贝次数
        if (tmp == null) { // 快速获取对象
            synchronized (SingletonV2.class) {
                if (tmp == null) { // 避免对象未初始化
                    tmp = new SingletonV2();
                    instance = tmp;
                }
            }
        }
        return instance;
    }
}
