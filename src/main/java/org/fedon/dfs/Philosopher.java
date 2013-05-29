package org.fedon.dfs;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Dmytro Fedonin
 *
 */
public class Philosopher {
    String name;
    Fork leftFork;
    Fork rightFork;
    boolean isMyTrun = true;
    static AtomicInteger maxWait = new AtomicInteger();

    public Philosopher(String name) {
        this.name = name;
        leftFork = new Fork();
        rightFork = new Fork();
    }

    public Philosopher(String name, Philosopher left) {
        this.name = name;
        leftFork = left.rightFork;
        rightFork = new Fork();
    }

    public Philosopher(String name, Philosopher left, Philosopher right) {
        this.name = name;
        leftFork = left.rightFork;
        rightFork = right.leftFork;
    }

    public void hungry() throws InterruptedException {
        long start = System.currentTimeMillis();
        System.out.println(name + " is hungry.");
        if (isMyTrun) { // insist
            if (!leftFork.take()) {
                waitForLeft();
            }
            if (!rightFork.take()) {
                waitForRight();
            }
        } else { // flexible
            if (leftFork.isFree.get() && rightFork.isFree.get()) { // eat without turn, start new queue
                boolean left = leftFork.take();
                boolean right = rightFork.take();
                if (!(left && right)) { // fail to eat, fall back
                    if (!right)
                        rightFork.put();
                    if (!left)
                        leftFork.put();
                    waitForTurn();
                    hungry();
                    return;
                }
            } else { // wait for turn
                waitForTurn();
                hungry();
                return;
            }
        }
        updateMaxWait(start); // eat
        full();
    }

    public void full() {
        leftFork.put();
        rightFork.put();

        isMyTrun = false;
    }

    void waitForLeft() {

    }

    void waitForRight() {

    }

    void waitForTurn() {

    }

    void updateMaxWait(long start) throws InterruptedException {
        long cur = System.currentTimeMillis();
        int curWait = (int) ((cur - start) / 1000);
        boolean result;
        do {
            result = false;
            int atom = maxWait.get();
            if (atom < curWait) {
                result = !maxWait.compareAndSet(atom, curWait);
            }

        } while (result);
        System.out.println(name + " is eating...\n --- waiting time: " + curWait + " --- max time: " + maxWait);
        wait(cur % 1000 * 10);
        System.out.println(name + " is full.");
    }

    class Fork {
        AtomicBoolean isFree = new AtomicBoolean(true);

        // boolean isFree = true;

        boolean take() { // TODO is sync
            return isFree.compareAndSet(true, false);
            // return true;
        }

        void put() {
            isFree.set(true);
        }
    }
}
