package samt.smajilbasic.deduplicator.scanner;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Source: https://stackoverflow.com/questions/9748710/how-to-pause-resume-all-threads-in-an-executorservice-in-java
 * Date: 14.02.2020
 * @author https://stackoverflow.com/users/471136/pathikrit 
 */
public class PausableExecutor extends ThreadPoolExecutor {
    private boolean isPaused = false;
    private Lock pauseLock = new ReentrantLock();
    private Condition unpaused = pauseLock.newCondition();

    public PausableExecutor(Integer threadCount, Integer timeout, TimeUnit timeUnit, ArrayBlockingQueue queue) {
        super(threadCount, threadCount, timeout, timeUnit, queue);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        pauseLock.lock();
        try {
            while (isPaused) {
                unpaused.await();
            }
        } catch (InterruptedException e) {
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
}
