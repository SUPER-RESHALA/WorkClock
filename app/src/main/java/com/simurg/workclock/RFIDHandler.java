package com.simurg.workclock;

import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

public class RFIDHandler {
    public static void RFIDInputHandler(EditText rfidNumber, Activity activity){
        rfidNumber.setOnEditorActionListener((v, actionId, event) -> {
            // Проверяем, что нажата клавиша Enter или действие "Done"
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                String enteredText = rfidNumber.getText().toString(); // Получаем введённый текст

                // Проверяем длину текста
                if (enteredText.length() == 10) {
                    processScannedData(enteredText, activity); // Если длина 10, обрабатываем данные
                } else {
                    Log.e("RFID_ERROR", "Некорректный ввод: " + enteredText); // Логируем ошибку
                }

                // Очищаем поле для нового ввода
                rfidNumber.setText("");
                return true; // Подтверждаем, что событие обработано
            }
            return false; // Если это не Enter или Done, не обрабатываем
        });
    }
    private static void processScannedData(String data, Activity activity) {
        // Здесь ваша логика обработки данных
        Toast.makeText(activity,data,Toast.LENGTH_SHORT).show();
        Log.d("ScannedData", data);
    }
}
