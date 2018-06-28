package com.webservice;

/**
 * Created by ketul.patel on 21/1/16.
 */
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

public class AsyncUtil {

    public AsyncUtil() {
        throw new RuntimeException("Only static methods are allowed!");
    }

    public static final void cancel(AsyncTask<?, ?, ?> task) {
        if (task != null && !task.isCancelled()) {
            task.cancel(true);
        }
    }

    @SuppressLint("NewApi")
    public static final <T> void execute(AsyncTask<T, ?, ?> task, T...t){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, t);
        }else{
            task.execute(t);
        }
    }
}