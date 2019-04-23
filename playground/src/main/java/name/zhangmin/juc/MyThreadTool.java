package name.zhangmin.juc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Java线程池原理示例类。请参考{@link java.util.concurrent.ThreadPoolExecutor}。
 *
 * @author zhangmin.name
 * @date 2019/4/18
 */
public class MyThreadTool {
    // 使用阻塞队列实现生产者-消费者模式：线程池使用方是生产者，线程池时消费者
    BlockingQueue<Runnable> workQueue;

    // 保存内部工作线程
    List<Worker> threads = new ArrayList<>();

    MyThreadTool(int poolSize, BlockingQueue workQueue) {
        this.workQueue = workQueue;
        // 创建工作线程
        for (int i = 0; i < poolSize; i++) {
            Worker worker = new Worker();
            worker.start();
            threads.add(worker);
        }
    }

    // 提交任务
    void execute(Runnable command) throws InterruptedException {
        workQueue.put(command);
    }

    class Worker extends Thread {
        // 工作线程负责消费任务，并执行任务
        public void run() {
            while(true) {
                Runnable task = null;
                try {
                    task = workQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                task.run();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue workQueue = new LinkedBlockingDeque(2);
        MyThreadTool pool = new MyThreadTool(10, workQueue);

        pool.execute(() -> {
            System.out.println("hello");
        });
    }

}
