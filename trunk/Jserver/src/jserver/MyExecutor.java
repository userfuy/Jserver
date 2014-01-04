package jserver;

import java.util.concurrent.*;

/**
 * Copyright (c) by <a href="userfuy@163.com">fuyong</a>
 *
 * @author <a href="userfuy@163.com">fuyong</a>
 * @version V1.0
 * @className: ${CLASS_NAME}
 * @description: ${todo}
 * @date 2014-01-02 下午10:43
 */
public class MyExecutor {
    private static final int THREAD_MAX_NUM = 20;
    private static final int SCHEDULED_THREAD_MAX_NUM = 5;
    private static MyExecutor instance;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    private MyExecutor() {
        threadPoolExecutor = new ThreadPoolExecutor(0, THREAD_MAX_NUM,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(SCHEDULED_THREAD_MAX_NUM);
    }

    synchronized public static MyExecutor getInstance() {
        if (null == instance) {
            instance = new MyExecutor();
        }
        return instance;
    }

    synchronized public Future<?> submit(Runnable task) {
        return threadPoolExecutor.submit(task);
    }

    public <T> Future<T> submit(Callable<T> task) {
        return threadPoolExecutor.submit(task);
    }

    public ScheduledFuture<?> schedule(Runnable task,
                                       long delay,
                                       TimeUnit unit) {

        return scheduledThreadPoolExecutor.schedule(task, delay, unit);
    }

    public <V> ScheduledFuture<V> schedule(Callable<V> task,
                                           long delay,
                                           TimeUnit unit) {

        return scheduledThreadPoolExecutor.schedule(task, delay, unit);
    }
}
