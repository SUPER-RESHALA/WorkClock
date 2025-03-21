package com.simurg.workclock.log;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class LogCatToFile {
    private static PrintStream logStream;
    private static  String LOG_FILE_NAME = "Exception.txt";

    public static void init(Context context) {
        try {
            File logFile = new File(context.getExternalFilesDir(null), LOG_FILE_NAME);
            logStream = new PrintStream(new FileOutputStream(logFile, true), true);

            // Перенаправляем System.out и System.err в файл
            System.setOut(logStream);
            System.setErr(logStream);

            // Логируем старт приложения
            log("=== APP STARTED: " + getCurrentTimestamp() + " ===");
            log("Device: " + Build.MANUFACTURER + " " + Build.MODEL + " (Android " + Build.VERSION.RELEASE + ")");

            // Устанавливаем глобальный обработчик исключений
            Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
                log("=== UNCAUGHT EXCEPTION ===");
                log("Thread: " + thread.getName());
                throwable.printStackTrace(logStream);
                log("=== APP CRASHED ===");
                logStream.flush();
                logStream.close();
                System.exit(1); // Принудительный выход
            });
        } catch (Exception e) {
            Log.e("LogToFile", "Ошибка инициализации логирования", e);
        }
    }

    public static void log(String message) {
        if (logStream != null) {
            logStream.println(getCurrentTimestamp() + " | INFO  | " + message);
            logStream.flush();
        }
        Log.i("APP_LOG", message);
    }
    public static String getLogFilePath(Context context) {
        File logFile = new File(context.getExternalFilesDir(null), LOG_FILE_NAME);
        return logFile.getAbsolutePath();
    }

    private static String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }
    public static void checkAndDeleteLogFile(String logFilePath,Context context) {
        File logFile = new File(logFilePath);

        if (logFile.exists() && logFile.length() > 1024 * 1024) { // 30 МБ
            if (logFile.delete()) {
                Log.i("LogManager", "Log file deleted: " + logFilePath);
                Random random = new Random();
                int randomInt = random.nextInt(1000000);
                LOG_FILE_NAME="Exceptions"+ randomInt+".txt";
                init(context);
            } else {
                Log.e("LogManager", "Failed to delete log file: " + logFilePath);
            }
        }
    }

}
