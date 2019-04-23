package name.zhangmin.juc;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangmin.name
 * @date 2019/4/23
 */
public class CompletableFutureExample {
    public static void main(String[] args) {
        // 描述AND汇聚任务
        // 任务 1：洗水壶 -> 烧开水
        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
            System.out.println("T1: 洗水壶...");
            sleep(1, TimeUnit.SECONDS);

            System.out.println("T1: 烧开水...");
            sleep(5, TimeUnit.SECONDS);
        });

        // 任务 2：洗茶壶 -> 洗茶杯 -> 拿茶叶
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("T2: 洗茶壶...");
            sleep(1, TimeUnit.SECONDS);

            System.out.println("T2: 洗茶杯...");
            sleep(1, TimeUnit.SECONDS);

            System.out.println("T2: 拿茶叶...");
            sleep(1, TimeUnit.SECONDS);
            return " 龙井 ";
        });

        // 任务 3：任务 1 和任务 2 完成后执行：泡茶
        CompletableFuture<String> f3 =
                f1.thenCombine(f2, (__, tf)->{
                    System.out.println("T1: 拿到茶叶:" + tf);
                    System.out.println("T1: 泡茶...");
                    return " 上茶:" + tf;
                });
        // 等待任务 3 执行结果
        System.out.println(f3.join());

        // 串行执行任务
        CompletableFuture<String> f0 = CompletableFuture.supplyAsync(() ->
            "hello world")
                .thenApply(s -> s + " ZM")
                .thenApply(String::toUpperCase);

        System.out.println(f0.join());

        // OR汇聚关系
        CompletableFuture<String> f4 =
                CompletableFuture.supplyAsync(()->{
                    int t = getRandom(5, 10);
                    sleep(t, TimeUnit.SECONDS);
                    return String.valueOf(t);
                });

        CompletableFuture<String> f5 =
                CompletableFuture.supplyAsync(()->{
                    int t = getRandom(5, 10);
                    sleep(t, TimeUnit.SECONDS);
                    return String.valueOf(t);
                });

        CompletableFuture<String> f6 =
                f4.applyToEither(f5, s -> s);

        System.out.println(f6.join());

        // 异常处理
        CompletableFuture f7 = CompletableFuture.supplyAsync(() -> 7/0)
                .thenApply(r -> r * 10)
                .exceptionally(e -> 0);

        System.out.println(f7.join());

    }

    public static void sleep(int t, TimeUnit u) {
        try {
            u.sleep(t);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static int getRandom(int low, int high) {
        return (int) (Math.random()* (high - low + 1) + low);
    }

}
