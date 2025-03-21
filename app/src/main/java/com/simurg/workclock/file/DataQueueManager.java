package com.simurg.workclock.file;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.simurg.workclock.RFIDHandler;
import com.simurg.workclock.data.DateTimeManager;
import com.simurg.workclock.entity.Employee;
import com.simurg.workclock.log.FileLogger;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class DataQueueManager {
    private final ConcurrentLinkedQueue<String> mainQueue = new ConcurrentLinkedQueue<>();

    private final AtomicBoolean isSyncing = new AtomicBoolean(false);
    private final AtomicBoolean isProcessingQueue = new AtomicBoolean(false);
    public boolean CheckIsProcessingQueue() {
        return isProcessingQueue.get();
    }

    public boolean CheckIsSyncing() {
        return isSyncing.get();
    }
    public boolean addDataToQueue(String data){
        return mainQueue.offer(data);
    }
    public void startSync() {
        isSyncing.set(true); // Устанавливаем флаг синхронизации
    }

    public void finishSyncAndProcessQueue(RFIDHandler rfidHandler, Activity activity, DateTimeManager dateTimeManager,
                                          String mainFolderName, File mainFolder, CsvReader csvReader ) {
        FileLogger.log("finishSyncAndProcessQu","method start");
        isSyncing.set(false);
        isProcessingQueue.set(true);

        // Обрабатываем основную очередь
        while (!mainQueue.isEmpty()) {
            FileLogger.log("finishSyncAndProcessQu", "while cycle,process queue");
            processDataDirectly(mainQueue.poll(),rfidHandler, activity,dateTimeManager,mainFolderName, mainFolder, csvReader);
        }
        isProcessingQueue.set(false);
    }

    private void processDataDirectly(String data, RFIDHandler rfidHandler, Activity activity, DateTimeManager dateTimeManager,
                                     String mainFolderName, File mainFolder, CsvReader csvReader) {
        // Обработка данных обычным способом
        FileLogger.log("processDataDirectly","Processing data: " + data );
        rfidHandler.processScannedDataFromQueue(data,activity,dateTimeManager,mainFolderName,mainFolder,csvReader);
        // Здесь вы добавите вашу логику обработки и записи в файлы
    }
}
