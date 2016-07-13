package com.fxtv.framework.thread;

import com.fxtv.framework.Profile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Var on 16/4/21.
 */
public class ThreadManager {

    private static ThreadManager mInstance;
    private ExecutorService mExecutor;

    private ThreadManager() {
        mExecutor = Executors.newCachedThreadPool();
    }

    public synchronized static ThreadManager getInstance() {
        if (mInstance == null) {
            synchronized (ThreadManager.class) {
                if (mInstance == null) {
                    mInstance = new ThreadManager();
                }
            }
        }

        return mInstance;
    }

    public void execute(ThreadTask task){
        mExecutor.submit(task);
    }
}
