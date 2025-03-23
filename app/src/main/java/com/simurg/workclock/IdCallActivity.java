package com.simurg.workclock;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.simurg.workclock.entity.DeviceId;
import com.simurg.workclock.file.FileManagerDesktop;
import com.simurg.workclock.log.FileLogger;

import java.util.Map;

public class IdCallActivity extends AppCompatActivity {
    private ActivityResultLauncher<String[]> permissionLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_layout);

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {

                    for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                        String permission = entry.getKey();
                        boolean isGranted = entry.getValue();
                        if (isGranted) {
                            Log.i("Permissions", "Разрешение предоставлено: " + permission);
                        } else {
                            FileLogger.logError("Permissions", "Permission denied "+ permission);
                            finish();
                        }
                    }
                }
        );
        requestPermissions();

        Button dialogSubmitBtn = findViewById(R.id.DialogSubmitBtn);
        EditText enterID = findViewById(R.id.DialogEnterField);
      dialogSubmitBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              DeviceId deviceId = new DeviceId(enterID.getText().toString());
              if (!deviceId.isValid()) {
                  Toast.makeText(IdCallActivity.this, "Неверный ввод ID устройства (должен содержать от 5 до 30 цифр)", Toast.LENGTH_SHORT).show();
              }else {
                  SharedPreferences prefs=getApplicationContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                  SharedPreferences.Editor editor=prefs.edit();
                  editor.putString("id",deviceId.getId());
                  editor.apply();
                  FileManagerDesktop.createCustomFolder(IdCallActivity.this,"WorkClockFiles");
              FileManagerDesktop.createFile(IdCallActivity.this,"WorkClockFiles","id.txt",deviceId.getId());
                  Intent intent = new Intent(IdCallActivity.this, MainActivity.class);
                  startActivity(intent); // Запускаем новую активность
                  finish();
              }
          }
      });


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