package com.simurg.workclock.Dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.simurg.workclock.R;
import com.simurg.workclock.entity.DeviceId;
import com.simurg.workclock.file.FileManagerDesktop;
import com.simurg.workclock.log.FileLogger;

import java.util.Objects;

public class Dialog {
    private DialogDeviceIdListener listener; // Ссылка на интерфейс

    // Метод для установки слушателя
    public void setDeviceIdListener(DialogDeviceIdListener listener) {
        this.listener = listener;
    }
    public void setupDialog(android.app.Dialog dialog) {
        dialog.setContentView(R.layout.dialog_layout);
        if (dialog.getWindow()==null){
            FileLogger.logError("SetupDialog", "dialog.getWindow Is null");
        }
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false); // Блокируем отмену диалога кнопкой "Назад".
    }

    public void handleDialogButtonClick(android.app.Dialog dialog, EditText enterID, Context context) {
        DeviceId deviceId = new DeviceId(enterID.getText().toString());
        if (!deviceId.isValid()) {
            Toast.makeText(dialog.getContext(), "Неверный ввод ID устройства (должен содержать от 5 до 30 цифр)", Toast.LENGTH_SHORT).show();
        } else {
//            FileManagerDesktop.createCustomFolder(context,"WorkClockFiles");
//            FileManagerDesktop.createFile(context,"WorkClockFiles","id.txt",deviceId.getId());
            dialog.dismiss(); // Закрываем диалог.
            // Передаем полученный DeviceId в MainActivity
            if (listener != null) {
                listener.onDeviceIdReceived(deviceId.getId());
            }
        }
    }

    public void showDialog(Context context) {
        android.app.Dialog dialog = new android.app.Dialog(context);
        setupDialog(dialog);
        // Ищем элементы интерфейса после настройки.
        Button dialogSubmitBtn = dialog.findViewById(R.id.DialogSubmitBtn);
        EditText enterID = dialog.findViewById(R.id.DialogEnterField);
        dialogSubmitBtn.setOnClickListener(v -> handleDialogButtonClick(dialog, enterID,context));
        dialog.show();
    }


}



//    public void setDialogSettings(android.app.Dialog dialog) {
//        dialog.setContentView(R.layout.dialog_layout);
//        Button dialogSubmitBtn =dialog.findViewById(R.id.DialogSubmitBtn);
//        EditText enterID= dialog.findViewById(R.id.DialogEnterField);
//        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.setCancelable(false);
//
//        dialogSubmitBtn.setOnClickListener(v -> {
//            DeviceId deviceId =new DeviceId(enterID.getText().toString());
//            if (!deviceId.isValid()) {
//                Toast.makeText(dialog.getContext(), "Неверный ввод ID устройства(должен содержать от 5 до 30 цифр)", Toast.LENGTH_SHORT).show();
//            } else {
//                //createFile(String.valueOf(enterID.getText()));
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//    }