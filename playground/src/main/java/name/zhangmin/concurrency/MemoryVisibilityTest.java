package name.zhangmin.concurrency;

/**
 * 缓存导致的线程可见性问题的示例。
 *
 * @author zhangmin.name
 * @date 2019/4/8
 */
public class MemoryVisibilityTest {
    private long count = 0;

    public void add10K() {
        int i = 0;
        while (i++ < 10_000) {
            count += 1;
        }
    }

    public long getCount() {
        return count;
    }

    public static void main(String[] args) {
        final MemoryVisibilityTest mvt = new MemoryVisibilityTest();
        Thread t1 = new Thread(() -> { mvt.add10K();});
        Thread t2 = new Thread(() -> { mvt.add10K();});

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("count: " + mvt.getCount()); // 多种情况 count: 14682; count: 20000
    }
}
