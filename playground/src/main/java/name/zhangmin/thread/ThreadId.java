package name.zhangmin.thread;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * Generates unique identifiers local to each thread.
 *
 * @author zhangmin.name
 * @date 2019/4/10
 */

public class ThreadId {
    // Atomic integer containing the next thread ID to be assigned
    private static final AtomicInteger nextId = new AtomicInteger(0);

    // Thread local variable containing each thread's ID
    private static final ThreadLocal<Integer> threadId =
            new ThreadLocal<Integer>() {
                @Override protected Integer initialValue() {
                    return nextId.getAndIncrement();
                }
            };

    // Returns the current thread's unique ID, assigning it if necessary
    public static int get() {
        return threadId.get();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                public void run() {
                        System.out.println(ThreadId.get());
                }
            }).start();
        }
    }
}
