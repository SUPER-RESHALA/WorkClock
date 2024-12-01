package com.simurg.workclock;


import android.Manifest;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.simurg.workclock.Dialog.Dialog;
import com.simurg.workclock.Dialog.DialogDeviceIdListener;
import com.simurg.workclock.file.FileManagerDesktop;

import java.util.Map;


public class MainActivity extends AppCompatActivity implements DialogDeviceIdListener{
    private ActivityResultLauncher<String[]> permissionLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        Dialog dialog = new Dialog();
        dialog.showDialog(this);
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
            dialog.setDeviceIdListener(this);
        EditText rfidNumber= findViewById(R.id.cardNumRFID);
        rfidNumber.setFocusable(true);
        rfidNumber.setFocusableInTouchMode(true);
        rfidNumber.setInputType(InputType.TYPE_NULL);
        RFIDHandler.RFIDInputHandler(rfidNumber,this);



// Скрываем поле для пользователя
        //editText.setVisibility(View.INVISIBLE);

// Добавляем слушатель на изменения текста


         FileManagerDesktop.createCustomFolder(this,"TESTDIRECTORY");


        System.out.println("Piper1");
        System.out.println("Piper2");
        String s="Piper3";
        System.out.println(s);
        System.out.println("Piper4");
//List<File> arrayList= FileManagerDesktop.getListAllFoldersInExternalFilesDir(this);
//        for (File file:arrayList) {
//            System.out.println(file);
//        }

    }


    @Override
    public void onDeviceIdReceived(String deviceId) {
        // Здесь вы получаете DeviceId или его строковое значение
        Log.i("MainActivity", "Полученный DeviceId: " + deviceId);
        FileManagerDesktop.createCustomFolder(this,"WorkClockFiles");
          FileManagerDesktop.createFile(this,"WorkClockFiles","id.txt",deviceId);
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

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });