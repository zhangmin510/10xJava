package name.zhangmin.concurrency;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 死锁演示。
 * @author zhangmin.name
 * @date 2019/4/8
 */
public class DeadLockExample extends Thread {
    private String first;
    private String second;

    public DeadLockExample(String name, String first, String second) {
        super(name);
        this.first = first;
        this.second = second;
    }

    @Override
    public void run() {
        synchronized (first) {
            System.out.println(this.getName() + " obtained: " + first);
            try {
                Thread.sleep(1000);
                synchronized (second) {
                   System.out.println(this.getName() + " obtained: " + second);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        // 检测死锁
        ThreadMXBean mbean = ManagementFactory.getThreadMXBean();
        Runnable deadLockChecker = () -> {
            long[] threadIds = mbean.findDeadlockedThreads();
            if (threadIds != null) {
                ThreadInfo[] threadInfos = mbean.getThreadInfo(threadIds);
                System.out.println("Detected deadlock threads:");
                for (ThreadInfo ti : threadInfos) {
                    System.out.println(ti.getThreadName());
                }
            }
        };

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(deadLockChecker, 5L, 10L, TimeUnit.SECONDS);

        // 死锁代码
        String lockA = "lockA";
        String lockB = "lockB";

        DeadLockExample t1 = new DeadLockExample("Thread-1", lockA, lockB);
        DeadLockExample t2 = new DeadLockExample("Thread-2", lockB, lockA);

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
