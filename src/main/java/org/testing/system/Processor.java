package org.testing.system;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Processor implements Runnable{

    private static final Lock tickEndLock = new ReentrantLock();
    private static final Condition tickEndCondition = tickEndLock.newCondition();
    private static final Lock tickLock = new ReentrantLock();
    private static final Condition tickCondition = tickLock.newCondition();

    private static int count =0;
    private static void next() {
        tickLock.lock();
        System.out.println("TICK: "+ ++count);
        tickCondition.signalAll();
        tickLock.unlock();
    }
    public static void waitTick() {
        tickEndLock.lock();
        try {
            tickEndCondition.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            tickEndLock.unlock();
        }
    }

    private static void endedTick() {
        tickEndLock.lock();
        tickEndCondition.signalAll();
        tickEndLock.unlock();
    }


    @Override
    public void run() {
        while (true) {
            next();
            endedTick();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
