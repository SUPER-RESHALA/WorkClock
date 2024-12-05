package com.simurg.workclock.thread;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {

    private ExecutorService executorService;
    private Handler mainHandler;

    public ThreadManager() {
        // Инициализация пула потоков с фиксированным числом потоков
        executorService = Executors.newFixedThreadPool(3); // Можно настроить количество потоков
        mainHandler = new Handler(Looper.getMainLooper());
    }

    // Метод для запуска фоновой задачи
    public void runBackgroundTask(Runnable task) {
        executorService.execute(task);
    }

    // Метод для выполнения задачи на главном потоке
    public void runOnMainThread(Runnable task) {
        mainHandler.post(task);
    }
    // Закрытие пула потоков
    public void shutDown() {
        executorService.shutdown();
    }
}