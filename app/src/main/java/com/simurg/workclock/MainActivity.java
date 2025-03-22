package com.simurg.workclock;



import static com.simurg.workclock.FileCollector.collectFiles;
import static com.simurg.workclock.ftp.FTPThreadTasks.sendFileToFtp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.simurg.workclock.Dialog.Dialog;
import com.simurg.workclock.Dialog.DialogDeviceIdListener;
import com.simurg.workclock.data.DateTimeManager;
import com.simurg.workclock.entity.Employee;
import com.simurg.workclock.file.CsvReader;
import com.simurg.workclock.file.DataQueueManager;
import com.simurg.workclock.file.FileManagerDesktop;
import com.simurg.workclock.ftp.FTPConnectionManager;
import com.simurg.workclock.ftp.FTPFileManager;
import com.simurg.workclock.ftp.FTPThreadTasks;
import com.simurg.workclock.log.FileLogger;
import com.simurg.workclock.log.LogCatToFile;
import com.simurg.workclock.network.NetworkUtils;
import com.simurg.workclock.template.HtmlEditor;
import com.simurg.workclock.thread.ThreadManager;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends AppCompatActivity {

    private String id;

    private DateTimeManager dateTimeManager;
    private TextView timeMain;
    private TextView dateMain;
    private EditText rfidNumber;
    private ThreadManager threadManager;
    private Handler handler;
    private Runnable timeUpdater;

    private FTPConnectionManager ftpConnectionManager;
    private ScheduledExecutorService scheduler;
    private static String mainFolderName = "WorkClockFiles";
    private ScheduledExecutorService mainSheduler;
    DataQueueManager dataQueueManager;
    RFIDHandler rfidHandler;
    CsvReader csvReader;
    private FTPFileManager ftpFileManager;
    File mainFolder;
public static AtomicInteger logNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        LogCatToFile.init(this);
        Log.e("999999999999999999999999", "CALLING  ONCREATE");
        FileLogger.init(this);



        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.black));
            window.setNavigationBarColor(Color.BLACK);
        }
        // Инициализация ActivityResultLauncher
        showDialog("WorkClockFiles", "id.txt");
        String filePath = this.getExternalFilesDir(null) + "/WorkClockFiles/cards.csv";
        File baseDir = this.getExternalFilesDir(null); // Базовая директория приложения
        mainFolder = new File(baseDir, mainFolderName);
        dataQueueManager= new DataQueueManager();
        csvReader= new CsvReader();
        threadManager = new ThreadManager();
        rfidNumber = findViewById(R.id.cardNumRFID);
        rfidNumber.setFocusable(true);
        rfidNumber.setFocusableInTouchMode(true);
        //Важная строка ниже ее потом влкючить
         rfidNumber.setInputType(InputType.TYPE_NULL);
        timeMain = findViewById(R.id.timeMain);
        dateMain = findViewById(R.id.dateMain);
        dateTimeManager = new DateTimeManager();
        timeMain.setText(dateTimeManager.getFormattedTime());
        dateMain.setText(dateTimeManager.getFormattedDate());
         rfidHandler = new RFIDHandler();
         FileManagerDesktop.renameAllTmpWithReplace(mainFolder,dateTimeManager);
        // FileManagerDesktop.deleteAllTmp(this,dateTimeManager);
        rfidHandler.RFIDInputHandler(rfidNumber, this, dateTimeManager,mainFolderName, mainFolder, csvReader,dataQueueManager);
        handler = new Handler();
        // Запускаем обновление времени каждую секунду
        startUpdatingTime();
      scheduler = Executors.newSingleThreadScheduledExecutor();
     // mainSheduler = Executors.newScheduledThreadPool(2);

        startUploadingFileEveryMinute(this, "WorkClockFiles", dateTimeManager, scheduler);


//FileManagerDesktop.renameAllTmpWithReplace(mainFolder,dateTimeManager);
//    File testFolder=   FileManagerDesktop.createCustomFolder(mainFolder,"testingFolder");
//    File testFile1=   FileManagerDesktop.createFileInCustomFolder(testFolder,"2.txt","abc");
//    File testFile2= FileManagerDesktop.createFileInCustomFolder(testFolder,"2local.txt","def");
//    FileManagerDesktop.renameFileWithReplace(testFile2.getAbsolutePath(),"2.txt");


        //   Map<String, Employee> map =csvReader.readCsvToMap(filePath);
    // FileManagerDesktop.createTemplateFile(this,map.get("0009771047"),mainFolderName,dateTimeManager,mainFolder);
      //FileManagerDesktop.createTemplateFile(this,map.get("0003830814"),mainFolderName,dateTimeManager,mainFolder);

       // startMainTasks(this,this,dateTimeManager,dataQueueManager,rfidHandler,csvReader,mainFolderName,mainFolder);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Проверяем состояние mainSheduler перед его созданием
        if (mainSheduler == null || mainSheduler.isShutdown()) {
            mainSheduler = Executors.newScheduledThreadPool(2);
            startMainTasks(this, this, dateTimeManager, dataQueueManager, rfidHandler, csvReader, mainFolderName, mainFolder);
        }
    }

    public void startMainTasks(Activity activity, Context context, DateTimeManager dateTimeManager, DataQueueManager dataQueueManager, RFIDHandler rfidHandler, CsvReader csvReader, String mainFolderName, File mainFolder){
        mainSheduler.scheduleWithFixedDelay(FTPThreadTasks.cardTask(activity,context,csvReader),0,2,TimeUnit.MINUTES);
        mainSheduler.scheduleWithFixedDelay(FTPThreadTasks.uploadTmpAndErrorTask(activity,context,dateTimeManager,dataQueueManager,rfidHandler,csvReader,mainFolderName,mainFolder),1,3,TimeUnit.MINUTES);
//        mainSheduler.scheduleWithFixedDelay(new Runnable() {
//            @Override
//            public void run() {
//                FTPThreadTasks.testFunction(context, mainSheduler);
//            }
//        }, 0, 4000, TimeUnit.MILLISECONDS);
       // mainSheduler.scheduleWithFixedDelay(FTPThreadTasks.testFunction2(context),0,60000,TimeUnit.MILLISECONDS);
    }
    private void showDialog(String folderName, String fileName) {

        File folder = new File(getExternalFilesDir(null), folderName);
        File file = new File(folder, fileName);
        if (!file.exists()) {

            // Если файла нет, запускаем IdCallActivity
            Intent intent = new Intent(MainActivity.this, IdCallActivity.class);
            startActivity(intent);
            finish();
             // Завершаем текущую активность, чтобы избежать возврата к MainActivity
            return;
        }

    }

    private void startUpdatingTime() {
        timeUpdater = new Runnable() {
            @Override
            public void run() {
                // Обновляем время каждую секунду
                timeMain.setText(dateTimeManager.getFormattedTime());

                // Обновляем дату, если она изменилась (ежедневно)
                String currentDate = dateTimeManager.getFormattedDate();
                if (!dateMain.getText().toString().equals(currentDate)) {
                    dateMain.setText(currentDate);
                }
                // Перезапуск через 1 секунду для времени
                handler.postDelayed(this, 1000);
            }
        };

        // Запуск обновления
        handler.post(timeUpdater);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Останавливаем обновление времени, когда активность уничтожена
        if (handler != null && timeUpdater != null) {
            handler.removeCallbacks(timeUpdater);
        }
        // Останавливаем планировщик задач
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        if (mainSheduler != null && !mainSheduler.isShutdown()) {
            mainSheduler.shutdown();
        }
    }

    public synchronized void startUploadingFileEveryMinute(Context context,  String localFolderName, DateTimeManager dateTimeManager, ScheduledExecutorService scheduler) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    sendFileToFtp(context, localFolderName, dateTimeManager);
                } catch (InterruptedException e) {
                    FileLogger.logError("startUploadingFileEveryMinute", e.getMessage());
                    throw new RuntimeException(e);
                }
               // FTPThreadTasks.checkCardFileModify(context);
            }
        };
        // Запуск задачи каждую минуту (60 секунд)
        scheduler.scheduleWithFixedDelay(task, 0, 1, TimeUnit.MINUTES);

    }
}
