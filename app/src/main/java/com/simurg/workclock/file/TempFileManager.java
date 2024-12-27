package com.simurg.workclock.file;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class TempFileManager {
    private final AtomicBoolean isCleaning = new AtomicBoolean(false);
    private final Queue<String> taskQueue = new ConcurrentLinkedQueue<>();

}
