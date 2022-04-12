package utils;

import io.netty.util.concurrent.FastThreadLocal;

public class FastThreadLocalDemo {

    private static Object obj = new Object();

    private static FastThreadLocal<Object> fastThreadLocal = new FastThreadLocal<Object>() {
        @Override
        protected Object initialValue() throws Exception {
            return new Object();
        }
    };

    public static void main(String[] args) {
        new Thread(() -> {
            // 独立读写
            System.out.println(fastThreadLocal.get());
            // 共同读写
            System.out.println(obj);
        }).start();

        new Thread(() -> {
            System.out.println(fastThreadLocal.get());
            System.out.println(obj);
        }).start();
    }
}
