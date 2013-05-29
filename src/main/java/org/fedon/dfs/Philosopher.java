package org.fedon.dfs;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @author Dmytro Fedonin
 *
 */
public class Philosopher extends Thread {
    String name;
    ReentrantLock leftFork;
    ReentrantLock rightFork;
    boolean isMyTrun = true;
    static AtomicInteger maxWait = new AtomicInteger();
    static AtomicInteger eating = new AtomicInteger();
    static AtomicInteger waiting = new AtomicInteger();
    static AtomicInteger thinking = new AtomicInteger();
    long start = 0;

    public Philosopher(String name) {
        this.name = name;
        leftFork = new ReentrantLock();
        rightFork = new ReentrantLock();
    }

    public Philosopher(String name, Philosopher left) {
        this.name = name;
        leftFork = left.rightFork;
        rightFork = new ReentrantLock();
    }

    public Philosopher(String name, Philosopher left, Philosopher right) {
        this.name = name;
        leftFork = left.rightFork;
        rightFork = right.leftFork;
    }

    public void hungry() throws InterruptedException {
        if (start == 0)
            start = System.currentTimeMillis();
        if (isMyTrun) { // insist
            leftFork.lock();
            rightFork.lock();
        } else { // flexible
            boolean mayEat = leftFork.tryLock();
            if (!mayEat) {
                waitForTurn();
                hungry();
                return;
            }
            mayEat &= rightFork.tryLock();
            if (!mayEat) {
                leftFork.unlock();
                waitForTurn();
                hungry();
                return;
            }
        } // eat without turn, start new eating queue
        updateMaxWait(); // eat
        full();
    }

    public void full() {
        notifyRight();

        leftFork.unlock();
        rightFork.unlock();

        isMyTrun = false;
    }

    void waitForTurn() throws InterruptedException {
        waitForLeft();
        isMyTrun = true;
    }

    void waitForLeft() throws InterruptedException {
        synchronized (leftFork) {
            leftFork.wait();
        }
    }

    void notifyRight() {
        synchronized (rightFork) {
            rightFork.notify();
        }
    }

    void updateMaxWait() throws InterruptedException {
        long cur = System.currentTimeMillis();
        int curWait = (int) ((cur - start + 500) / 1000);
        boolean result;
        do {
            result = false;
            int atom = maxWait.get();
            if (atom < curWait) {
                result = !maxWait.compareAndSet(atom, curWait);
            }

        } while (result);
        start = 0;
        waiting.decrementAndGet();
        System.out.println(name + " is eating... + " + eating.incrementAndGet() + "\n --- waiting time: " + curWait + " --- max time: " + maxWait);
        sleep(cur % 1000 * 3); // max eating 3 sec
        eating.decrementAndGet();
        System.out.println(name + " is thinking. * " + thinking.incrementAndGet());
    }

    public void run() {
        System.out.println(name + " starts thinking. - " + thinking.incrementAndGet());
        try {
            while (true) {
                sleep(System.currentTimeMillis() % 1000 * 3); // max thinking 3 sec
                thinking.decrementAndGet();
                System.out.println(name + " is hungry. # " + waiting.incrementAndGet());
                hungry();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}