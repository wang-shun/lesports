package io.socket.thread;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


/**
 * The thread for event loop. All non-background tasks run within this thread.
 */
public class EventThread extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(EventThread.class);
    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private static final ExecutorService sigle = Executors.newSingleThreadExecutor();
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable runnable) {
            thread = new EventThread(runnable);
            return thread;
        }
    };

    private static EventThread thread;

    private static ExecutorService service;

    private static int counter = 0;


    private EventThread(Runnable runnable) {
        super(runnable);
    }

    /**
     * check if the current thread is EventThread.
     *
     * @return true if the current thread is EventThread.
     */
    public static boolean isCurrent() {
        return currentThread() == thread;
    }

    /**
     * Executes a task in EventThread.
     *
     * @param task
     */
    public static void exec(Runnable task) {
        try {
            executorService.execute(task);

        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
    }

    /**
     * Executes a task on the next loop in EventThread.
     *
     * @param task
     */
    public static void nextTick(final Runnable task) {
        ExecutorService executor;
        synchronized (EventThread.class) {
            counter++;
            if (service == null) {
                service = Executors.newSingleThreadExecutor(THREAD_FACTORY);
            }
            executor = service;
        }

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    task.run();
                } finally {
                    synchronized (EventThread.class) {
                        counter--;
                        if (counter == 0) {
                            service.shutdown();
                            service = null;
                            thread = null;
                        }
                    }
                }
            }
        });
    }
}
