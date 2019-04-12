package name.zhangmin.juc;

import java.util.Vector;
import java.util.concurrent.*;

/**
 * @author zhangmin.name
 * @date 2019/4/12
 */
public class ThreadSynchronizedExample {
    public static void main(String[] args) {
        Executor executor = Executors.newFixedThreadPool(2);

        // 一个线程等待多个线程，计数器不能重置使用
        CountDownLatch latch = new CountDownLatch(2);

        executor.execute(() -> {
            System.out.println("operation 1");
            latch.countDown();
        });
        executor.execute(() -> {
            System.out.println("operation 2");
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("all operations done");

        // 一组线程之间互相等待， 计数器可以循环使用
        Vector v1 = new Vector();
        Vector v2 = new Vector();

        // 大小为1的固定线程池， 保证顺序处理, 两个队列一对一读取，防止错乱
        Executor executor1 = Executors.newFixedThreadPool(1);
        final CyclicBarrier barrier = new CyclicBarrier(2, () -> {
            executor1.execute(() -> {
                Object o1 = v1.remove(0);
                Object o2 = v2.remove(0);
                System.out.println("operation 3 using operation 1 and 2's result");
            });
        });

        Thread t1 = new Thread(() -> {
            v1.add(new Object());
            System.out.println("operation 1 get object");
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        });
        t1.start();

        Thread t2 = new Thread(() -> {
            v2.add(new Object());
            System.out.println("operation 2 get object");
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        });
        t2.start();

    }
}
