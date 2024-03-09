package com.github.paicoding.forum.web.front.test.rest;

import java.util.HashSet;

/**
 * @author: zhujun
 * @description: TODO
 * @date: 2024/3/7 21:25
 */
public class SynchronizedTest {
    private static int counter = 0;

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new IncrementTask());
        Thread t2 = new Thread(new IncrementTask());
        ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Counter: " + counter);
    }

    private static class IncrementTask implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 1000; i++) {
                synchronized (SynchronizedTest.class) {
                    counter++;
                }
            }
        }
    }

}
