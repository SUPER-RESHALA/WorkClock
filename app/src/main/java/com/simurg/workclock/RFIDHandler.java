package com.simurg.workclock;

import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.simurg.workclock.data.DateTimeManager;
import com.simurg.workclock.entity.Employee;
import com.simurg.workclock.file.CsvReader;
import com.simurg.workclock.file.FileManagerDesktop;

import java.io.File;
import java.util.Map;

public class RFIDHandler {
    private long initCsvFileSize =-1;
    private File csvFile;
    Map<String, Employee> map;
    public void RFIDInputHandler(EditText rfidNumber, Activity activity, DateTimeManager dateTimeManager, String mainFolderName, File mainFolder, CsvReader csvReader){
        rfidNumber.setOnEditorActionListener((v, actionId, event) -> {
            // Проверяем, что нажата клавиша Enter или действие "Done"
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                String enteredText = rfidNumber.getText().toString(); // Получаем введённый текст

                // Проверяем длину текста
                if (enteredText.length() == 10) {
                    processScannedData(enteredText, activity,  dateTimeManager, mainFolderName, mainFolder, csvReader); // Если длина 10, обрабатываем данные
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
    private  void processScannedData(String data, Activity activity, DateTimeManager dateTimeManager, String mainFolderName, File mainFolder, CsvReader csvReader) {
        // Здесь ваша логика обработки данных
       // Toast.makeText(activity,data,Toast.LENGTH_SHORT).show();
        Log.d("ScannedData", data);
        Employee employee;
        csvFile= new File(mainFolder, "cards.csv");
        if (csvReader.checkIsUpdating() || !csvFile.exists() ){
            System.out.println("UPDATING_-___-_-_-__-_-_-_-___-_-_-_-__---__-__-");
            File errorFile=new File(mainFolder,"error.txt");
            if (!errorFile.exists()){
                FileManagerDesktop.createFile(activity,mainFolderName,"error.txt",data+"  "+ dateTimeManager.getFormattedTime()+" "+dateTimeManager.getFormattedDate()+ "\n");
            }else {FileManagerDesktop.writeToFile(errorFile,data+"  "+ dateTimeManager.getFormattedTime()+" "+dateTimeManager.getFormattedDate()+ "\n");
            }
            return;
        }
        if (csvFile.exists() && csvFile!=null){
            if(csvFile.length() != initCsvFileSize){
                initCsvFileSize= csvFile.length();
                map=csvReader.readCsvToMap(csvFile.getAbsolutePath());
                Log.i( "-----------------", "FILE CHANGED");
            }else {
                System.out.println("File doesnt changed, no need to read");
            }
             }else {
            Log.e("processScannedData", "File not exist");
        }
        if (map.isEmpty()){
            employee=null;
        }else {
            employee= map.get(data);
        }


      //  employee=csvMap.get(data);
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
