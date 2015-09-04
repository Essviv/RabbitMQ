package com.cmcc.syw;

/**
 * Created by sunyiwei on 2015/9/4.
 */
public class Utils {
    private static String DEFAULT_MSG = "defaultMsg";

    public static String getMsg(String[] args){
        if(args.length < 1){
            return DEFAULT_MSG;
        }

        return joinStr(args);
    }

    public static String joinStr(String[] args){
        StringBuilder sb = new StringBuilder(args[0]);

        for (int i = 1; i < args.length; i++) {
            sb.append(".").append(args[i]);
        }

        return sb.toString();
    }

    public static void doWork(String msg) throws InterruptedException {
        for (char c : msg.toCharArray()) {
            if (c == '.') {
                System.out.println(Thread.currentThread().getName() + "sleeps...zzzz...");
                Thread.sleep(1000);
            }
        }
    }
}
