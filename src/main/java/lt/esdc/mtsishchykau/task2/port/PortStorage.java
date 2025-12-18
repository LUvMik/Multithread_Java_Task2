package lt.esdc.mtsishchykau.task2.port;

import lt.esdc.mtsishchykau.task2.container.Container;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PortStorage {
    private final Deque<Container> deque = new ArrayDeque<>();

    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition notEmpty = lock.newCondition();

    public int size() {
        lock.lock();
        try {
            return deque.size();
        } finally {
            lock.unlock();
        }
    }

    public void put(Container container) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            deque.addLast(container);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public Container take() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            if (deque.isEmpty()) {
                return null;
            }
            return deque.removeFirst();
        } finally {
            lock.unlock();
        }
    }
}
