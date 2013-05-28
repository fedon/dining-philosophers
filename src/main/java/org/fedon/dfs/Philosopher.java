package org.fedon.dfs;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @author Dmytro Fedonin
 *
 */
public class Philosopher {
    String name;
    Fork leftFork;
    Fork rightFork;
    boolean isMyTrun = true;

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

    public void hungry() {
        if (isMyTrun) {
            if (leftFork.take()) {
                rightFork.take();
            } else {
                // TODO sync
            }
        } else {
            if (leftFork.isFree.get() && rightFork.isFree.get()) {
                leftFork.take();
                rightFork.take();
            }
        }
    }

    public void full() {
        leftFork.put();
        rightFork.put();

        isMyTrun = false;
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
