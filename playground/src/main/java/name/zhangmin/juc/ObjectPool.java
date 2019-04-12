package name.zhangmin.juc;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import java.util.function.Function;

/**
 * 对象池的实现，基于Semaphore实现。
 *
 * @author zhangmin
 * @date 2019/4/8
 */
public class ObjectPool<T, R> {
    final List<T> pool;
    final Semaphore sem;

    ObjectPool(int size, T t) {
        pool = new Vector<T>();
        for (int i = 0; i < size; i++) {
            pool.add(t);
        }
        sem = new Semaphore(size);
    }

    R exec(Function<T, R> func) throws InterruptedException {
        T t = null;
        sem.acquire();
        try {
            t = pool.remove(0);
            return func.apply(t);
        } finally {
            pool.add(t);
            sem.release();
        }
    }

    public static void main(String[] args) {
        ObjectPool<Long, String> pool = new ObjectPool<Long, String>(3, 2L);
        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        pool.exec(t -> {
                            System.out.println(Thread.currentThread().getName() +
                                    " running at " + System.currentTimeMillis()/1000);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            return t.toString();
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + " stopped");
                }
            }, "thread-" + i).start();
        }
    }
}
