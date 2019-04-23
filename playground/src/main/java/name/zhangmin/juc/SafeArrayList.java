package name.zhangmin.juc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 将非线程安全的 {@link ArrayList} 封装成为线程安全的.
 *
 * 主要思路：将非线程安全的容器封装在对象内部，并控制好对其的
 * 访问路径就可以了。
 *
 * {@link Collections}中就是这样实现对ArrayList，HashSet和HashMap
 * 进行线程安全封装的。
 *
 * @see Collections
 * @author zhangmin.name
 * @date 2019/4/16
 */
public class SafeArrayList<T> {
    List<T> list = new ArrayList<T>();

    public synchronized T get(int index) {
        return list.get(index);
    }

    public synchronized void add(int index, T t) {
        list.add(index, t);
    }

    public synchronized boolean addIfNotExist(T t) {
        if (list.contains(t)) {
            return false;
        }

        list.add(t);
        return true;
    }

    public static void main(String[] args) {
        List list = Collections.synchronizedList(new ArrayList<>());
        list.add(510L);

        CopyOnWriteArrayList<Integer> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
        copyOnWriteArrayList.add(100);
    }
}
