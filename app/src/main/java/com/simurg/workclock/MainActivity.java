package com.simurg.workclock;


import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.simurg.workclock.Dialog.DialogDeviceIdListener;
import com.simurg.workclock.file.FileManagerDesktop;
import com.simurg.workclock.ftp.FTPConnectionManager;
import com.simurg.workclock.network.NetworkUtils;
import com.simurg.workclock.template.HtmlEditor;

import java.util.Map;

//interface X {
//    String abc(Integer a, Date b);
//}

public class MainActivity extends AppCompatActivity implements DialogDeviceIdListener {
    //    String xyz(Integer a, Date b) {
//        return "asdfasdf" + a + b;
//    }
    private ActivityResultLauncher<String[]> permissionLauncher;
    private String id;

    //    void test(X x) {
//        System.out.println(x.abc(123, new Date()));
//    }
//        test(this::xyz);
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        // Инициализация ActivityResultLauncher
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    // Обработка результатов
                    for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                        String permission = entry.getKey();
                        boolean isGranted = entry.getValue();
                        if (isGranted) {
                            Log.i("Permissions", "Разрешение предоставлено: " + permission);
                        } else {
                            Log.e("Permissions", "Разрешение отклонено: " + permission);
                            finish();
                        }
                    }
                }
        );
        requestPermissions();
//        Dialog dialog = new Dialog();
//        dialog.setDeviceIdListener(this);
//        dialog.showDialog(this);
//        ThreadManager threadManager=new ThreadManager();
//        EditText rfidNumber = findViewById(R.id.cardNumRFID);
//        rfidNumber.setFocusable(true);
//        rfidNumber.setFocusableInTouchMode(true);
//        rfidNumber.setInputType(InputType.TYPE_NULL);
//        RFIDHandler.RFIDInputHandler(rfidNumber, this);
//        TextView timeMain = findViewById(R.id.timeMain);
//        TextView dateMain = findViewById(R.id.dateMain);
//       DateTimeManager dateTimeManager = new DateTimeManager();
//        timeMain.setText(dateTimeManager.getFormattedTime());
//        dateMain.setText(dateTimeManager.getFormattedDate());







//        FTPConnectionManager ftpConnectionManager=new FTPConnectionManager();
//        ftpConnectionManager.connect(FTPConnectionManager.hostname);
//        ftpConnectionManager.login(FTPConnectionManager.user,FTPConnectionManager.password);
//
//        ftpConnectionManager.logout();
//        ftpConnectionManager.disconnect();

        // Выполнение в отдельном потоке
        new Thread(() -> {
            FTPConnectionManager ftpConnectionManager = new FTPConnectionManager();
            try {
                ftpConnectionManager.connect(FTPConnectionManager.hostname);

                if (ftpConnectionManager.isConnected()) {
                    ftpConnectionManager.login(FTPConnectionManager.user, FTPConnectionManager.password);
                    System.out.println("FTP login successful!");

                    // Выполнение операций с сервером

                } else {
                    System.err.println("Failed to connect to FTP server.");
                }
            } catch (Exception e) {
                e.printStackTrace(); // Для отладки
            } finally {
                try {
                    ftpConnectionManager.logout();
                    ftpConnectionManager.disconnect();
                } catch (Exception e) {
                    System.err.println("Error during logout or disconnect: " + e.getMessage());
                }
            }
        }).start();



        System.out.println("TEST1");
        System.out.println("TEST2");
        String s = "TEST3";
        System.out.println(s);
        System.out.println("TEST4");


    }


    @Override
    public void onDeviceIdReceived(String deviceId) {
        // Здесь вы получаете DeviceId или его строковое значение
        Log.i("MainActivity", "Полученный DeviceId: " + deviceId);
        FileManagerDesktop.createCustomFolder(this, "WorkClockFiles");
        FileManagerDesktop.createFile(this, "WorkClockFiles", deviceId + ".txt", deviceId);
        // WorkClockFiles


    }

    private void requestPermissions() {
        String[] permissions = {
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CHANGE_WIFI_STATE,
                android.Manifest.permission.ACCESS_WIFI_STATE,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET
        };
        permissionLauncher.launch(permissions);
    }


}
//сделай проверку html файла перед загрузкой
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });