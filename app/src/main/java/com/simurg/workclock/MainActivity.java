package com.simurg.workclock;



import static com.simurg.workclock.FileCollector.collectFiles;
import static com.simurg.workclock.ftp.FTPThreadTasks.sendFileToFtp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
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
    DataQueueManager dataQueueManager;
CsvReader csvReader;
    private FTPFileManager ftpFileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        // Инициализация ActivityResultLauncher
        showDialog("WorkClockFiles", "id.txt");
        String filePath = this.getExternalFilesDir(null) + "/WorkClockFiles/cards.csv";
        File baseDir = this.getExternalFilesDir(null); // Базовая директория приложения
        File mainFolder = new File(baseDir, mainFolderName);
dataQueueManager= new DataQueueManager();
        csvReader= new CsvReader();
        threadManager = new ThreadManager();
        rfidNumber = findViewById(R.id.cardNumRFID);
        rfidNumber.setFocusable(true);
        rfidNumber.setFocusableInTouchMode(true);
        rfidNumber.setInputType(InputType.TYPE_NULL);
        timeMain = findViewById(R.id.timeMain);
        dateMain = findViewById(R.id.dateMain);
        dateTimeManager = new DateTimeManager();
        timeMain.setText(dateTimeManager.getFormattedTime());
        dateMain.setText(dateTimeManager.getFormattedDate());
        RFIDHandler rfidHandler = new RFIDHandler();
      rfidHandler.RFIDInputHandler(rfidNumber, this, dateTimeManager,mainFolderName, mainFolder, csvReader,dataQueueManager);
        handler = new Handler();
        // Запускаем обновление времени каждую секунду
        startUpdatingTime();
      scheduler = Executors.newSingleThreadScheduledExecutor();
     startUploadingFileEveryMinute(this, "WorkClockFiles", dateTimeManager, scheduler);


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



//    private void startUpdErrFile(File mainFolder) {
//        Runnable errUpdater = new Runnable() {
//            @Override
//            public void run() {
//                FTPConnectionManager ftpConnectionManagerErr = new FTPConnectionManager();
//                try {
//                    ftpConnectionManagerErr.connect(FTPConnectionManager.hostname);
//                    ftpConnectionManagerErr.login(FTPConnectionManager.user,FTPConnectionManager.password);
//                    FTPFileManager ftpFileManagerErr = new FTPFileManager(ftpConnectionManagerErr.getFtpClient());
//                    if ( ftpFileManagerErr.getCurrentWorkingDirectory()!="/settings"){
//                        ftpFileManagerErr.navigateToParentDirectory();
//                        ftpFileManagerErr.changeWorkingDirectory("settings/");
//                        File errFile = new File(mainFolder.getAbsolutePath(),"error.txt");
//                        if (ftpFileManagerErr.uploadFile(errFile.getAbsolutePath())){
//                            ftpConnectionManagerErr.disconnect();
//                        }
//                    }
//                }catch (Exception e){
//                   Log.e("errUpdater", "Some error in this method or connection troubles");
//                }finally {
//                    ftpConnectionManagerErr.disconnect();
//                }
//              // Перезапуск через 6 минут для времени
//                handler.postDelayed(this,360000);
//            }
//        };
//
//        // Запуск обновления
//        handler.post(errUpdater);
//    }

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
    }

    public synchronized void startUploadingFileEveryMinute(Context context,  String localFolderName, DateTimeManager dateTimeManager, ScheduledExecutorService scheduler) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    sendFileToFtp(context, localFolderName, dateTimeManager);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
               // FTPThreadTasks.checkCardFileModify(context);
            }
        };
        // Запуск задачи каждую минуту (60 секунд)
        scheduler.scheduleWithFixedDelay(task, 0, 1, TimeUnit.MINUTES);

    }

}
