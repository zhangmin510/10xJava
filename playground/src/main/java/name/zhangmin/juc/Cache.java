package name.zhangmin.juc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 通用的缓存工具类，基于读写锁实现
 *
 * @author zhangmin.name
 * @date 2019/4/8
 */
public class Cache<K, V> {
    final Map<K, V> map = new HashMap<>();

    final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    final Lock readLock = readWriteLock.readLock();
    final Lock writeLock = readWriteLock.writeLock();

    V get(K key) {
        readLock.lock();
        try {
            return map.get(key);
        } finally {
            readLock.unlock();
        }
    }

    // 实现缓存的按需加载
    V getOnDemand(K key) {
        V v = null;
        // read cache
        readLock.lock();
        try {
            v = map.get(key);
        } finally {
            readLock.unlock();
        }

        // cache hit, return
        if (v != null) {
            return v;
        }

        // cache miss load cache
        writeLock.lock();
        // if cache miss, there may be multiple thread query db for data
        try {
            v = map.get(key);
            // check cache again, avoid duplicated query db
            if (v == null) {
                // load cache from db
                // v = xxx
                map.put(key, v);
            }
        } finally {
            writeLock.unlock();
        }
        return v;
    }


    V put(K key, V value) {
        writeLock.lock();
        try {
            return map.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    public static void main(String[] args) {
        final Cache<String, String> cache = new Cache<>();
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    long id = Thread.currentThread().getId();
                    String key = Thread.currentThread().getName();
                    String value = Long.toString(id);
                    long timestamp = System.currentTimeMillis();
                    for (int j = 0; j < 10; j++) {
                        if (id % 2 == 0) {
                            System.out.println(timestamp + " put key: " + key + " with value: " + Long.toString(id));
                            cache.put(key, value);
                        } else {
                            System.out.println(timestamp + " get key: " + key + " with value:" + cache.get(key));
                        }
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " stopped");
            }, "thread-" + i).start();
        }
    }

}
