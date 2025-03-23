package com.simurg.workclock.log;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileLogger  {

    private static final String LOG_TAG = "FileLogger";
    private static  String LOG_FILE_NAME = "app_logs.txt";
    private static final String LOG_FOLDER_NAME="LOGS";
    private static File logFile;

    public static void init(Context context) {
        Log.i("initLogger", "Init logger");
        File logDir = new File(context.getExternalFilesDir(null), LOG_FOLDER_NAME);
       // File logDir = new File(context.getFilesDir(), "logs");
        if (!logDir.exists()) {
            logDir.mkdirs();  // Создание директории для логов
        }
        SharedPreferences prefs= context.getApplicationContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String time="time"+System.currentTimeMillis();
        String id ="idIs"+prefs.getString("id",time);
        if (!id.equals(time)){id+=time;}
        LOG_FILE_NAME= "app_logs"+id+".txt";
        logFile = new File(logDir, LOG_FILE_NAME);
    }

    public static void log(String tag, String message) {
        logToFile(tag, message);
        Log.d(tag, message);  // Параллельно выводим в консоль
    }

    public static void logError(String tag, String message) {
        logToFile(tag, "ERROR: " + message);
        Log.e(tag, message);
    }

    private static void logToFile(String tag, String message) {
        if (logFile == null) {
            Log.e(LOG_TAG, "Logger not initialized. Call FileLogger.init(context) first.");
            return;
        }

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String logMessage = timeStamp + " [" + tag + "] " + message + "\n";
synchronized (FileLogger.class){
    try(BufferedWriter writer = new BufferedWriter(new FileWriter(logFile,true))){
        writer.append(logMessage);
        writer.flush();
    }catch(IOException e){
        Log.e(LOG_TAG, "Failed to write log to file", e);
    }
}
    }

    // Метод для получения пути к файлу логов (например, для отправки на сервер)
    public static String getLogFilePath() {
        return logFile != null ? logFile.getAbsolutePath() : null;
    }
public static void checkAndDeleteLog(Context context){
        File logFile= new File(getLogFilePath());
        if (logFile.exists() && logFile.length() > 5* 1024 * 1024){
           if (logFile.delete()){
               LOG_FILE_NAME= "app_logs"+System.currentTimeMillis()+".txt";
               init(context);
           }else {
               FileLogger.logError("checkAndDeleteLog", "cant delete File "+ logFile.getAbsolutePath());
           }
        }
}
public static boolean checkLogOverflow(){
    File logFile= new File(getLogFilePath());
    return logFile.length() > 5 * 1024 * 1024;
}
    public static boolean isLogExist() {
        if (getLogFilePath() == null) {
            FileLogger.logError("isLogExist", "Log file not exist");
            return false;  // Файл не существует
        }
        return true;  // Файл существует
    }

public static boolean deleteLogFile(Context context){
    File logFile= new File(getLogFilePath());
    if (logFile.delete()){
       // LOG_FILE_NAME= "app_logs"+System.currentTimeMillis()+".txt";
        init(context);
        return true;
    }
    FileLogger.logError("checkAndDeleteLog", "cant delete File "+ logFile.getAbsolutePath());
    return false;
}
}