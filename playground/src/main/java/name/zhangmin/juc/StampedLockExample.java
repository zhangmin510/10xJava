package name.zhangmin.juc;

import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.StampedLock;

/**
 * @author zhangmin.name
 * @date 2019/4/10
 */
public class StampedLockExample {
    public static void main(String[] args) throws InterruptedException {
        final StampedLock lock = new StampedLock();

        Thread t1 = new Thread(() -> {
            // get write lock
            lock.writeLock();
            // blocked never release write lock
            LockSupport.park();
        });

        t1.start();

        // ensure t1 get lock
        Thread.sleep(1000);

        Thread t2 = new Thread(() -> {
           lock.readLock();
        });

        t2.start();

        // ensure t2 get lock
        Thread.sleep(1000);

        // interrupt t2, cause t2 cpu rising
        t2.interrupt();
        t2.join();
    }
}
