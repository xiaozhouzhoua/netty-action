package utils;

import io.netty.util.concurrent.FastThreadLocal;

import java.util.concurrent.TimeUnit;

/**
 * FastThreadLocal一个线程的修改不影响其它线程
 */
public class FastThreadLocalDemo2 {

    private static FastThreadLocal<Object> fastThreadLocal = new FastThreadLocal<Object>() {
        @Override
        protected Object initialValue() throws Exception {
            return new Object();
        }
    };

    public static void main(String[] args) {
        new Thread(() -> {
            Object object = fastThreadLocal.get();
            System.out.println(object);
            while (true) {
                fastThreadLocal.set(new Object());
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();

        new Thread(() -> {
            Object object = fastThreadLocal.get();
            System.out.println(object);
            while (true) {
                // 不管其它线程如何修改，该线程中永远与第一次拿出来的对象一致
                System.out.println(fastThreadLocal.get() == object);
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
