package com.simurg.workclock.file;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class TempFileManager {
    private final AtomicBoolean isTmpUpdating = new AtomicBoolean(false);
    private final Queue<String> taskQueue = new ConcurrentLinkedQueue<>();
    private final  AtomicBoolean isTaskQueueCleaning = new AtomicBoolean(false);

}
