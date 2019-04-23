package name.zhangmin.juc;

import java.util.concurrent.*;

/**
 * @author zhangmin.name
 * @date 2019/4/20
 */
public class FutureExample {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        FutureTask<Integer> futureTask = new FutureTask<>(() -> 1 + 2);

        executorService.submit(futureTask);

        Integer result = null;
        try {
            result = futureTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println(result);


        FutureExample futureExample = new FutureExample();
        futureExample.run();
    }

    public void run() {
        FutureTask<String> ft2 = new FutureTask<>(new T2Task());
        FutureTask<String> ft1 = new FutureTask<>(new T1Task(ft2));

        Thread t1 = new Thread(ft1);
        t1.start();
        Thread t2 = new Thread(ft2);
        t2.start();

        try {
            System.out.println(ft1.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    // T1Task 需要执行的任务：
    // 洗水壶、烧开水、泡茶
    class T1Task implements Callable<String> {
        FutureTask<String> ft;

        T1Task(FutureTask<String> ft) {
            this.ft = ft;
        }

        @Override
        public String call() throws Exception {
            System.out.println("T1: 洗水壶");
            TimeUnit.SECONDS.sleep(1);

            System.out.println("T1: 烧开水");
            TimeUnit.SECONDS.sleep(8);

            String result = ft.get();
            System.out.println("T1: 拿到茶叶: " + result);

            System.out.println("T1: 泡茶");
            return "上茶:" + result;
        }
    }

    // T2Task 需要执行的任务:
    // 洗茶壶、洗茶杯、拿茶叶
    class T2Task implements Callable<String> {
        @Override
        public String call() throws Exception {
            System.out.println("T2: 洗茶壶...");
            TimeUnit.SECONDS.sleep(1);

            System.out.println("T2: 洗茶杯...");
            TimeUnit.SECONDS.sleep(2);

            System.out.println("T2: 拿茶叶...");
            TimeUnit.SECONDS.sleep(1);
            return " 龙井 ";
        }
    }
}
