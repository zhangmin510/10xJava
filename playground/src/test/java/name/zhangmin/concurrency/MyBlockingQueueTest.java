package name.zhangmin.concurrency;

import org.junit.Test;

import static org.junit.Assert.*;

public class MyBlockingQueueTest {

    @Test
    public void normalTest() {
        MyBlockingQueue<Integer> queue = new MyBlockingQueue<>(2);
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        System.out.println("queue blocked never come here until dequeue");
    }

}