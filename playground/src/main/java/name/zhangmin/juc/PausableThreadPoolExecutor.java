package name.zhangmin.juc;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 扩展{@link ThreadPoolExecutor}来实现自定义功能, 来自官方javadoc示例。
 *
 * @author zhangmin.name
 * @date 2019/4/18
 */
public class PausableThreadPoolExecutor extends ThreadPoolExecutor {
    private boolean isPaused;
    private ReentrantLock pauseLock = new ReentrantLock();
    private Condition unpaused = pauseLock.newCondition();

    PausableThreadPoolExecutor(int corePoolSize,
                               int maximumPoolSize,
                               long keepAliveTime,
                               TimeUnit unit,
                               BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        pauseLock.lock();
        try {
            while (isPaused) unpaused.await();
        } catch (InterruptedException ie) {
            t.interrupt();
        } finally {
            pauseLock.unlock();
        }
    }

    public void pause() {
        pauseLock.lock();
        try {
            isPaused = true;
        } finally {
            pauseLock.unlock();
        }
    }

    public void resume() {
        pauseLock.lock();
        try {
            isPaused = false;
            unpaused.signalAll();
        } finally {
            pauseLock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        PausableThreadPoolExecutor pausableThreadPoolExecutor =
                new PausableThreadPoolExecutor(2,
                        2, 1000,
                        TimeUnit.MICROSECONDS, new ArrayBlockingQueue<>(2));
        pausableThreadPoolExecutor.pause();

        pausableThreadPoolExecutor.submit(() -> {
            System.out.println("hello");
        });

        Thread.sleep(2000);

        pausableThreadPoolExecutor.resume();
    }
}
