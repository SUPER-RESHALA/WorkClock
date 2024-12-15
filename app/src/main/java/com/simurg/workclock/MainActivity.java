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

        threadManager = new ThreadManager();
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

        handler = new Handler();
        // Запускаем обновление времени каждую секунду
        startUpdatingTime();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // startUploadingFileEveryMinute(this, "WorkClockFiles", dateTimeManager, scheduler);

        String filePath = this.getExternalFilesDir(null) + "/WorkClockFiles/cards.csv";
        //ftpFileManager.downloadFile("cards.csv" ,filePath);
//      Map<String, Employee> map = CsvReader.readCsvToMap(filePath);
//
//       Employee employee = map.get("0003830814");
//        ArrayList<Employee>employees = new ArrayList<>();
//        employees.add(map.get("0003830814"));
//        employees.add(map.get("0011401031"));
//        employees.add(map.get("0006388485"));
//        employees.add(map.get("0007381830"));
//        employees.add(map.get("0007503145"));
//        employees.add(map.get("0005148229"));
//        employees.add(map.get("0006360843"));

//
//
        
        
        
       // System.out.println("ВЫВОД----------------ОБЪЕКТА");
       //System.out.println(employee.getCode() + " " + employee.getSubdivision());

      // System.out.println(employee.getCode() + " " + employee.getSubdivision());
//        System.out.println(employee.getCode() + " " + employee.getSubdivision());
//        System.out.println(employee.getCode() + " " + employee.getSubdivision());
//        System.out.println(employee.getCode() + " " + employee.getSubdivision());
        File baseDir = this.getExternalFilesDir(null); // Базовая директория приложения
        File mainFolder = new File(baseDir, mainFolderName);
     
        //FileManagerDesktop.createTemplateFile(this,employee," ", dateTimeManager, mainFolder);

//testDelete(ftpConnectionManager,ftpFileManager);

//        for (Employee empl:employees) {
//            FileManagerDesktop.createTemplateFile(this,empl,mainFolderName,dateTimeManager, mainFolder);
//        }

   // FileManagerDesktop.createTemplateFile(this,employee,mainFolderName,dateTimeManager, mainFolder);
        File firstFile= new File(mainFolder,"/416.html");
        //   File secondFile= new File(mainFolder,"/261.html");

      //  System.out.println(FileManagerDesktop.readFileContenFromFile(secondFile));

//        try {
//            HtmlEditor.mergeFiles(this,firstFile,secondFile);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }


        File base = this.getExternalFilesDir(null); // Базовая директория приложения
        File mainF = new File(base, mainFolderName+"/2024"+ "/12.2024");
//File testRead= new File(mainF,"/C_4/392.html");
//
//        try {
//            System.out.println(HtmlEditor.extractDataRowsFromFileOldVersion(testRead));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }


        //FileManagerDesktop.renameFile(TestFile.getAbsolutePath(),"TESTFILE");

        List<String> relativePaths = new ArrayList<>();

        // Получаем массив файлов
        List<File> files = collectFiles(mainF, "", relativePaths);
        System.out.println("---------------------------------------------------------------------------------");
        // Вывод относительных путей
        System.out.println("Относительные пути:");
        for (String path : relativePaths) {
            System.out.println(path);
        }
        System.out.println("---------------------------------------------------------------------------------");

        // Вывод полного пути каждого файла
        System.out.println("\nАбсолютные пути файлов:");
        for (File file : files) {
            System.out.println(file.getAbsolutePath());
        }


//
//        new Thread(()->{
//            FTPThreadTasks.uploadAllTmp(this,dateTimeManager);
//        }).start();



        System.out.println("TEST1");
        System.out.println("TEST2");
        String s = "TEST3";
        System.out.println(s);
        System.out.println("TEST4");


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
        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.MINUTES);

    }

}
