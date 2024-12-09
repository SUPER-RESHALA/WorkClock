package com.simurg.workclock;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.simurg.workclock.file.FileManagerDesktop;
import com.simurg.workclock.ftp.FTPConnectionManager;
import com.simurg.workclock.ftp.FTPFileManager;
import com.simurg.workclock.network.NetworkUtils;
import com.simurg.workclock.thread.ThreadManager;

import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity  {

    private String id;

    private  DateTimeManager dateTimeManager;
    private TextView timeMain;
    private TextView dateMain;
private  EditText rfidNumber;
    private ThreadManager threadManager;
    private Handler handler;
    private Runnable timeUpdater;
    private  FTPConnectionManager ftpConnectionManager;

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

        showDialog("WorkClockFiles","id.txt");

        threadManager=new ThreadManager();
        rfidNumber = findViewById(R.id.cardNumRFID);
        rfidNumber.setFocusable(true);
        rfidNumber.setFocusableInTouchMode(true);
        rfidNumber.setInputType(InputType.TYPE_NULL);
        RFIDHandler.RFIDInputHandler(rfidNumber, this);
         timeMain = findViewById(R.id.timeMain);
         dateMain = findViewById(R.id.dateMain);
        dateTimeManager = new DateTimeManager();
      timeMain.setText(dateTimeManager.getFormattedTime());
       dateMain.setText(dateTimeManager.getFormattedDate());


       ftpConnectionManager=new FTPConnectionManager();
       ftpFileManager=new FTPFileManager(ftpConnectionManager.getFtpClient());

        this.id = FileManagerDesktop.readFileContent(this,"WorkClockFiles","id.txt");
        String ID=FileManagerDesktop.readFileContent(this,"WorkClockFiles","id.txt");
       // Toast.makeText(this,"FILE  "+CONTENT,Toast.LENGTH_LONG).show();
        handler = new Handler();
        // Запускаем обновление времени каждую секунду
        startUpdatingTime();
FileManagerDesktop.createFile(this,"WorkClockFiles","555555555.txt",dateTimeManager.getFormattedTime()+" "+dateTimeManager.getFormattedDate());
startUploadingFileEveryMinute(this,ftpConnectionManager,ftpFileManager, "WorkClockFiles",ID+".txt",dateTimeManager);

        System.out.println("TEST1");
        System.out.println("TEST2");
        String s = "TEST3";
        System.out.println(s);
        System.out.println("TEST4");


    }


    private void showDialog(String folderName, String fileName){

        File folder = new File(getExternalFilesDir(null), folderName);
        File file = new File(folder, fileName);
        if (!file.exists()) {
            // Если файла нет, запускаем IdCallActivity
            Intent intent = new Intent(MainActivity.this, IdCallActivity.class);
            startActivity(intent);
            finish(); // Завершаем текущую активность, чтобы избежать возврата к MainActivity
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
    }


    private void sendFileToFtp(Context context, FTPConnectionManager ftpConnectionManager, FTPFileManager ftpFileManager, String localFolderName, String filename, DateTimeManager dateTimeManager) {
        if (NetworkUtils.isNetworkConnected(context)) {
            File localFile = new File(context.getExternalFilesDir(localFolderName), filename);

            if (!localFile.exists()) {
                Log.e("FTP", "Файл для загрузки не существует: " + localFile.getAbsolutePath());
                return;
            }

            new Thread(() -> {
                try {
                    ftpConnectionManager.connect(FTPConnectionManager.hostname);
                    ftpConnectionManager.login(FTPConnectionManager.user, FTPConnectionManager.password);

                    Log.i("FTP", "Текущая рабочая директория: " + ftpFileManager.getCurrentWorkingDirectory());
                    if (!Objects.equals(ftpFileManager.getCurrentWorkingDirectory(), "settings/")){
                        ftpFileManager.navigateToParentDirectory();
                        ftpFileManager.changeWorkingDirectory("settings/");
                    }


                    Log.i("FTP", "Рабочая директория после смены: " + ftpFileManager.getCurrentWorkingDirectory());
                    Log.i("Send  File TO FTP  CURRENT TIME IS",dateTimeManager.getFormattedTime());
                    boolean uploadSuccess = ftpFileManager.uploadFile(localFile.getAbsolutePath());
                    if (uploadSuccess) {
                        Log.i("FTP", "Файл успешно загружен: " + localFile.getName());
                    } else {
                        Log.e("FTP", "Ошибка загрузки файла: " + localFile.getName());
                    }
                } catch (Exception e) {
                    Log.e("FTP", "Ошибка при работе с FTP: " + e.getMessage(), e);
                } finally {
                    ftpConnectionManager.disconnect();
                }
            }).start();
        } else {
            Log.e("FTP", "Нет подключения к интернету.");
        }
    }



    public void startUploadingFileEveryMinute(Context context, FTPConnectionManager ftpConnectionManager, FTPFileManager ftpFileManager, String localFolderName, String filename, DateTimeManager dateTimeManager) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable task = new Runnable() {
            @Override
            public void run() {
                sendFileToFtp(context, ftpConnectionManager, ftpFileManager, localFolderName, filename, dateTimeManager);

            }
        };

        // Запуск задачи каждую минуту (60 секунд)
        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.MINUTES);

        // Сохраните ссылку на scheduler, чтобы иметь возможность остановить его позже
        // scheduler.shutdown(); // Останавливайте scheduler, когда он больше не нужен
    }




}
//сделай проверку html файла перед загрузкой
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });