package com.simurg.workclock;

import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.simurg.workclock.data.DateTimeManager;
import com.simurg.workclock.entity.Employee;
import com.simurg.workclock.file.FileManagerDesktop;

import java.io.File;
import java.util.Map;

public class RFIDHandler {
    public static void RFIDInputHandler(EditText rfidNumber, Activity activity,  Map<String, Employee> csvMap, DateTimeManager dateTimeManager, String mainFolderName, File mainFolder){
        rfidNumber.setOnEditorActionListener((v, actionId, event) -> {
            // Проверяем, что нажата клавиша Enter или действие "Done"
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                String enteredText = rfidNumber.getText().toString(); // Получаем введённый текст

                // Проверяем длину текста
                if (enteredText.length() == 10) {
                    processScannedData(enteredText, activity, csvMap, dateTimeManager, mainFolderName, mainFolder); // Если длина 10, обрабатываем данные
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
    private static void processScannedData(String data, Activity activity, Map<String, Employee> csvMap, DateTimeManager dateTimeManager, String mainFolderName, File mainFolder) {
        // Здесь ваша логика обработки данных
       // Toast.makeText(activity,data,Toast.LENGTH_SHORT).show();
        Log.d("ScannedData", data);
        Employee employee = new Employee();
        employee=csvMap.get(data);
        File errorFile=new File(mainFolder,"error.txt");
        if (employee==null){
            if (!errorFile.exists()){
                FileManagerDesktop.createFile(activity,mainFolderName,"error.txt",data+"  "+ dateTimeManager.getFormattedTime()+" "+dateTimeManager.getFormattedDate()+ "\n");
            }else {FileManagerDesktop.writeToFile(errorFile,data+"  "+ dateTimeManager.getFormattedTime()+" "+dateTimeManager.getFormattedDate()+ "\n");
            }
            Toast.makeText(activity,"Время зафиксировано В ERROR TXT",Toast.LENGTH_SHORT).show();
        }else {
            FileManagerDesktop.createTemplateFile(activity,employee,mainFolderName,dateTimeManager,mainFolder);
            Toast.makeText(activity,"Время зафиксировано  "+ employee.getCode(),Toast.LENGTH_SHORT).show();
        }


    }
}
