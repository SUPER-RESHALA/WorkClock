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
import com.simurg.workclock.file.DataQueueManager;
import com.simurg.workclock.file.FileManagerDesktop;

import java.io.File;
import java.util.Map;

public class RFIDHandler {
    private long initCsvFileSize =-1;
    private File csvFile;
    Map<String, Employee> map;
    public void RFIDInputHandler(EditText rfidNumber, Activity activity, DateTimeManager dateTimeManager, String mainFolderName, File mainFolder, CsvReader csvReader, DataQueueManager dataQueueManager){
        rfidNumber.setOnEditorActionListener((v, actionId, event) -> {
            // Проверяем, что нажата клавиша Enter или действие "Done"
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                String enteredText = rfidNumber.getText().toString(); // Получаем введённый текст

                // Проверяем длину текста
                if (enteredText.length() == 10) {
                    addData(enteredText,activity,dateTimeManager,mainFolderName,mainFolder,csvReader,dataQueueManager);
                    //processScannedData(enteredText, activity,  dateTimeManager, mainFolderName, mainFolder, csvReader); // Если длина 10, обрабатываем данные
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
    private  synchronized void processScannedDataOld(String data, Activity activity, DateTimeManager dateTimeManager, String mainFolderName, File mainFolder, CsvReader csvReader) {
        // Здесь логика обработки данных
       // Toast.makeText(activity,data,Toast.LENGTH_SHORT).show();
        Log.d("ScannedData", data);
        Employee employee;
        csvFile= new File(mainFolder, "cards.csv");
        File errorFile=new File(mainFolder,"error.txt");
        if (csvReader.checkIsUpdating() || !csvFile.exists() ){
            Log.w("ProcessScannedData","Csv Файл не существует или обновляется" );
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


    private synchronized void processScannedData(String data, Activity activity, DateTimeManager dateTimeManager, String mainFolderName, File mainFolder, CsvReader csvReader) {
        Log.d("ScannedData", "Обработка данных: " + data);
        csvFile = new File(mainFolder, "cards.csv");
        File errorFile = new File(mainFolder, "error.txt");
        // Проверяем обновление или отсутствие файла
        if (isCsvUpdatingOrMissing(csvReader, csvFile, errorFile, data, activity, dateTimeManager, mainFolderName)) {
            return;
        }
        // Проверяем изменения в CSV-файле
        updateCsvIfChanged(csvReader, csvFile);

        // Получаем сотрудника из карты
        Employee employee = findEmployeeInMap(data, csvReader);

        // Обрабатываем результат
        processEmployeeResult(employee, data, errorFile, activity, dateTimeManager, mainFolderName, mainFolder);
    }

    private boolean isCsvUpdatingOrMissing(CsvReader csvReader, File csvFile, File errorFile, String data, Activity activity, DateTimeManager dateTimeManager, String mainFolderName
    ) {
        if (csvReader.checkIsUpdating() || !csvFile.exists()) {
            Log.w("ProcessScannedData", "Csv-файл не существует или обновляется");
            appendToErrorFile(errorFile, data, activity, dateTimeManager, mainFolderName);
            return true;
        }
        return false;
    }

    private void updateCsvIfChanged(CsvReader csvReader, File csvFile) {
        if (csvFile.length() != initCsvFileSize) {
            initCsvFileSize = csvFile.length();
            map = csvReader.readCsvToMap(csvFile.getAbsolutePath());
            Log.i("ProcessScannedData", "Файл изменился, Map обновлен.");
        } else {
            Log.i("ProcessScannedData", "Файл не изменился, Map не обновляется.");
        }
    }

    private Employee findEmployeeInMap(String data, CsvReader csvReader) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        return map.get(data);
    }

    private void processEmployeeResult(
            Employee employee,
            String data,
            File errorFile,
            Activity activity,
            DateTimeManager dateTimeManager,
            String mainFolderName,
            File mainFolder
    ) {
        if (employee == null) {
            appendToErrorFile(errorFile, data, activity, dateTimeManager, mainFolderName);
            Toast.makeText(activity, "Время зафиксировано в ERROR TXT", Toast.LENGTH_SHORT).show();
        } else {
            FileManagerDesktop.createTemplateFile(activity, employee, mainFolderName, dateTimeManager, mainFolder);
            Toast.makeText(activity, "Время зафиксировано: " + employee.getCode(), Toast.LENGTH_SHORT).show();
        }
    }

    private void appendToErrorFile(
            File errorFile,
            String data,
            Activity activity,
            DateTimeManager dateTimeManager,
            String mainFolderName
    ) {
        String timestamp = data + " " + dateTimeManager.getFormattedTime() + " " + dateTimeManager.getFormattedDate() + "\n";
        if (!errorFile.exists()) {
            FileManagerDesktop.createFile(activity, mainFolderName, "error.txt", timestamp);
        } else {
            FileManagerDesktop.writeToFile(errorFile, timestamp);
        }
    }



    public void addData(String data, Activity activity, DateTimeManager dateTimeManager, String mainFolderName, File mainFolder, CsvReader csvReader, DataQueueManager dataQueueManager) {
        String dateTime="|"+dateTimeManager.getFormattedTime()+"|"+dateTimeManager.getFormattedDate();
        if (dataQueueManager.CheckIsSyncing() || dataQueueManager.CheckIsProcessingQueue()) {
            activity.runOnUiThread(() ->
                    Toast.makeText(activity, "Время зафиксировано для "+ data+" идет синхронизация с сервером, данные добавятся позже", Toast.LENGTH_SHORT).show()
            );
            dataQueueManager.addDataToQueue(data+dateTime); // Добавляем в основную очередь
        }  // Добавляем во временную очередь
        else {
            processScannedData(data,activity,dateTimeManager,mainFolderName,mainFolder,csvReader); // Прямо сейчас безопасно обработать данные
        }
    }


    public synchronized void processScannedDataFromQueue(String data, Activity activity, DateTimeManager dateTimeManager, String mainFolderName, File mainFolder, CsvReader csvReader) {
        // Здесь логика обработки данных
        // Toast.makeText(activity,data,Toast.LENGTH_SHORT).show();
        String[] workerInfo = FileManagerDesktop.splitString(data, "|");
        Log.d("ScannedData", data+ " "+ workerInfo[0]);
        Employee employee;
        csvFile= new File(mainFolder, "cards.csv");
        File errorFile=new File(mainFolder,"error.txt");
        if (csvReader.checkIsUpdating() || !csvFile.exists() ){
            Log.w("ProcessScannedData","Csv Файл не существует или обновляется" );
            if (!errorFile.exists()){
                FileManagerDesktop.createFile(activity,mainFolderName,"error.txt",workerInfo[0]+"  "+ workerInfo[1]+" "+workerInfo[2]+ "\n");
            }else {FileManagerDesktop.writeToFile(errorFile,workerInfo[0]+"  "+ workerInfo[1]+" "+workerInfo[2]+ "\n");
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
            employee= map.get(workerInfo[0]);
        }
        //  employee=csvMap.get(data);
        if (employee==null){
            if (!errorFile.exists()){
                FileManagerDesktop.createFile(activity,mainFolderName,"error.txt",workerInfo[0]+"  "+ workerInfo[1]+" "+workerInfo[2]+ "\n");
            }else {FileManagerDesktop.writeToFile(errorFile,workerInfo[0]+"  "+ workerInfo[1]+" "+workerInfo[2]+ "\n");
            }
         //  Toast.makeText(activity,"Время зафиксировано В ERROR TXT",Toast.LENGTH_SHORT).show();
            activity.runOnUiThread(() ->
                    Toast.makeText(activity, "Время зафиксировано В ERROR TXT", Toast.LENGTH_SHORT).show()
            );

        }else {
            FileManagerDesktop.createTemplateFileForQueue(activity,employee,mainFolderName,dateTimeManager,mainFolder,data );
         //   Toast.makeText(activity,"Время зафиксировано  "+ employee.getCode(),Toast.LENGTH_SHORT).show();
            activity.runOnUiThread(() ->
                    Toast.makeText(activity, "Время зафиксировано для " + employee.getCode(), Toast.LENGTH_SHORT).show()
            );
        }


    }




}
