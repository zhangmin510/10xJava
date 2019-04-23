package name.zhangmin.juc;

import name.zhangmin.thread.ThreadId;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 基于原子类实现计数器功能。
 *
 * @author zhangmin.name
 * @date 2019/4/16
 */
public class Counter {
    AtomicLong count = new AtomicLong(0);

    void add10K() {
        int idx = 0;
        while(idx++ < 10000) {
            count.getAndIncrement();
        }
    }

    long get() {
        return count.get();
    }

    public static void main(String[] args) {
        final Counter counter = new Counter();
        CountDownLatch latch = new CountDownLatch(5);
        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                public void run() {
                    counter.add10K();
                    latch.countDown();
                }
            }).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(counter.get());
    }
}
