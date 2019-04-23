package name.zhangmin.juc;

/**
 * 模拟CAS操作。
 *
 * @author zhangmin.name
 * @date 2019/4/16
 */
public class MyCAS {
    volatile int count;

    void addOne() {
        int newValue = 0;
        do {
            newValue = count;
        } while(count != cas(count, newValue + 1));
    }

    // 只用于理解原理，CAS实现是不能用锁的，要使用CPU的CAS指令
    synchronized int cas(int expect, int newValue) {
        // 防止频繁读取volatile的值，因为读取volatile可能触发缓存刷新操作
        int curValue = count;

        if (curValue == expect) {
            count = newValue;
        }
        return curValue;
    }
}
