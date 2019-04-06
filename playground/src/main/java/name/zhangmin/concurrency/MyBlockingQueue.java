package name.zhangmin.concurrency;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyBlockingQueue<T> {
    LinkedList<T> queue;
    int size;
    final Lock lock = new ReentrantLock();
    // 队列不满
    final Condition notFull = lock.newCondition();
    // 队列不空
    final Condition notEmpty = lock.newCondition();

    MyBlockingQueue(int size) {
        this.queue = new LinkedList<T>();
        this.size = size;
    }

    public void enqueue(T e) {
        lock.lock();
        try {
            // 如果队列已满，则等待阻塞
            while (this.queue.size() >= this.size) {
                notFull.await();
            }
            this.queue.add(e);
            this.size++;
            // 入队后， 就可以出队了
            notEmpty.signal();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public T dequeue() {
        lock.lock();
        try {
            while (this.queue.isEmpty()) {
                notEmpty.await();
            }
            T e = this.queue.remove();
            notFull.signal();
            return e;
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        } finally {
            lock.unlock();
        }
        return null;
    }
}
