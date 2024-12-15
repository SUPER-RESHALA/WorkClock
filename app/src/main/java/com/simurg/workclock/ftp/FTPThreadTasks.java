package com.simurg.workclock.ftp;

import static com.simurg.workclock.FileCollector.collectFiles;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.simurg.workclock.data.DateTimeManager;
import com.simurg.workclock.file.FileManagerDesktop;
import com.simurg.workclock.network.NetworkUtils;
import com.simurg.workclock.template.HtmlEditor;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FTPThreadTasks {
    public static synchronized void checkCardFileModify(Context context  ) {
     new Thread(()->{
String TAG="checkCardFileModify";
     int replyCode;
         String remoteFileName = "cards.csv";
    // String remoteFilePath = "/settings/cards.csv"; // Абсолютный путь к файлу на сервере
     String localFolderName = "WorkClockFiles"; // Локальная папка для хранения файла
         FTPConnectionManager ftpConnectionManager = new FTPConnectionManager();
         FTPFileManager ftpFileManager = new FTPFileManager(ftpConnectionManager.getFtpClient());
     ftpConnectionManager.getFtpClient().enterLocalPassiveMode(); // Пассивный режим

    try {
        ftpConnectionManager.connect(FTPConnectionManager.hostname);
        ftpConnectionManager.login(FTPConnectionManager.user, FTPConnectionManager.password);
        if (!Objects.equals(ftpFileManager.getCurrentWorkingDirectory(), "settings/")){
        ftpFileManager.navigateToParentDirectory();
        ftpFileManager.changeWorkingDirectory("settings/");
    }
        Log.i(TAG, "Начало проверки модификации файла на сервере.");

        // Получаем информацию о файле на сервере
        FTPFile[] files = ftpConnectionManager.getFtpClient().listFiles(remoteFileName);
        replyCode = ftpConnectionManager.getFtpClient().getReplyCode();
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            Log.e(TAG, "Не удалось получить список файлов. Код ответа: " + replyCode);
            return;
        } else {
            Log.i(TAG, "Список файлов на сервере получен. Код ответа: " + replyCode);
        }

        if (files.length > 0) {
            Log.i(TAG, "Файл найден на сервере.");
            long remoteFileSize = files[0].getSize();

            // Локальная директория и файл
            File localFolder = new File(context.getExternalFilesDir(null), localFolderName);
            if (!localFolder.exists() && !localFolder.mkdirs()) {
                Log.e(TAG, "Не удалось создать локальную папку: " + localFolder.getAbsolutePath());
                return;
            }

            File localFile = new File(localFolder, "cards.csv");

            // Проверяем наличие локального файла и его размер
            if (localFile.exists() && remoteFileSize != localFile.length()) {
                Log.i(TAG, "Локальный файл отличается от файла на сервере. Заменяем его.");
                try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(localFile))) {
                    if (ftpConnectionManager.getFtpClient().retrieveFile(remoteFileName, outputStream)) {
                        Log.i(TAG, "Файл успешно обновлен: " + localFile.getAbsolutePath());
                    } else {
                        Log.e(TAG, "Не удалось обновить файл. Код ответа: " + ftpConnectionManager.getFtpClient().getReplyCode());
                    }
                }
            } else if (!localFile.exists()) {
                Log.i(TAG, "Локальный файл отсутствует. Скачиваем его.");
                try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(localFile))) {
                    if (ftpConnectionManager.getFtpClient().retrieveFile(remoteFileName, outputStream)) {
                        Log.i(TAG, "Файл успешно скачан: " + localFile.getAbsolutePath());
                    } else {
                        Log.e(TAG, "Не удалось скачать файл. Код ответа: " + ftpConnectionManager.getFtpClient().getReplyCode());
                    }
                }
            } else {
                Log.i(TAG, "Локальный файл идентичен файлу на сервере. Никаких действий не требуется.");
            }
        } else {
            Log.e(TAG, "Файл не найден на сервере: " + remoteFileName);
        }
    } catch (IOException e) {
        Log.e(TAG, "Ошибка при проверке или загрузке файла: " + e.getMessage(), e);
    }finally {
        ftpConnectionManager.disconnect();
        Log.i(TAG, "Соединение с FTP-сервером закрыто.");
    }
}).start();


 }

    public static synchronized void sendFileToFtp(Context context, String localFolderName, DateTimeManager dateTimeManager) throws InterruptedException {
        if (NetworkUtils.isNetworkConnected(context)) {
            new Thread(() -> {
                FTPConnectionManager ftpConnectionManager = new FTPConnectionManager();
                FTPFileManager ftpFileManager =new FTPFileManager(ftpConnectionManager.getFtpClient());
                try {
                     File idFile= new File(context.getExternalFilesDir(null),localFolderName+"/id.txt");
                     if (!idFile.exists()){
                         Log.e("sendFileToFTP", "Id TXT не существует" );
                         return;
                     }
                    String fileName= FileManagerDesktop.readFileContent(context,localFolderName,"id.txt")+".txt";
                    String fileContent= dateTimeManager.getFormattedTime()+" "+dateTimeManager.getFormattedDate();
                    File localFile = FileManagerDesktop.createFile(context,localFolderName,fileName,fileContent);
                    if (localFile == null || !localFile.exists()) {
                        Log.e("sendFileToFtp", "Файл для загрузки не создан: " + (localFile != null ? localFile.getAbsolutePath() : "null"));
                        return;
                    }
                    ftpConnectionManager.connect(FTPConnectionManager.hostname);
                    ftpConnectionManager.login(FTPConnectionManager.user, FTPConnectionManager.password);
                    Log.i("sendFileToFtp", "Текущая рабочая директория: " + ftpFileManager.getCurrentWorkingDirectory());
                    if (!Objects.equals(ftpFileManager.getCurrentWorkingDirectory(), "settings/")){
                        ftpFileManager.navigateToParentDirectory();
                        ftpFileManager.changeWorkingDirectory("settings/");
                    }
                    Log.i("sendFileToFtp", "Рабочая директория после смены: " + ftpFileManager.getCurrentWorkingDirectory());
                    Log.i("Send  File TO FTP  CURRENT TIME IS",dateTimeManager.getFormattedTime());
                    boolean uploadSuccess = ftpFileManager.uploadFile(localFile.getAbsolutePath());
                    if (uploadSuccess) {
                        Log.i("sendFileToFtp", "Файл успешно загружен: " + localFile.getName());
                    } else {
                        Log.e("sendFileToFtp", "Ошибка загрузки файла: " + localFile.getName());
                    }
                } catch (Exception e) {
                    Log.e("sendFileToFtp", "Ошибка при работе с FTP: " + e.getMessage(), e);
                } finally {
                    ftpConnectionManager.disconnect();
                }
            }).start();

        }else {
            Log.e("SEND FILE TO FTP", "NO INTERNET");
        }
    }

    public static void uploadAllTmp(Context context, DateTimeManager dateTimeManager){
        FTPConnectionManager ftpConnectionManager = new FTPConnectionManager();
        FTPFileManager ftpFileManager = new FTPFileManager(ftpConnectionManager.getFtpClient());
        String mainFolderName ="WorkClockFiles";
        String currentYear= dateTimeManager.getYear();
        String currentMonthYear= dateTimeManager.getFormattedMonthYear();
        String ftpMonthDir=  currentYear+ "/"+currentMonthYear;
        File baseDir = context.getExternalFilesDir(null); // Базовая директория приложения
        File mainF = new File(baseDir, mainFolderName+"/" + currentYear+ "/"+currentMonthYear);
        List<String> relativePaths = new ArrayList<>();// относительные пути файлов
        // Получаем массив файлов
        List<File> files = collectFiles(mainF, "", relativePaths);
        try{
         ftpConnectionManager.connect(FTPConnectionManager.hostname);
         ftpConnectionManager.login(FTPConnectionManager.user,FTPConnectionManager.password);
         for (int i=0; i<files.size();i++){
             String relativePath= relativePaths.get(i);
             String fullPath=ftpMonthDir+relativePath.substring(0,relativePath.lastIndexOf("/"));
             File currentFile= files.get(i);
             String fileName =currentFile.getName();
            ftpFileManager.moveCurrentDir(ftpFileManager,fullPath);
             if (ftpFileManager.fileExists(fileName)){
               String newLocalFileFullPath=mainFolderName+"/"+ currentYear+ "/"+currentMonthYear + relativePath.substring(0,relativePath.lastIndexOf("/")) + "/" +fileName.substring(0,fileName.indexOf("."))+"local.html";
               String newLocalFilePath=newLocalFileFullPath.substring(0, newLocalFileFullPath.lastIndexOf("/"));
               File localFile= new File (baseDir,newLocalFileFullPath);
               File finalLocalFile= new File(baseDir,newLocalFilePath+"/"+fileName);
               FileManagerDesktop.renameFile(currentFile.getAbsolutePath(), fileName.substring(0,fileName.indexOf("."))+"local.html");
             if (ftpFileManager.downloadFile(fileName,finalLocalFile.getAbsolutePath())){
                 HtmlEditor.mergeFiles(context,localFile,finalLocalFile);
                 ftpFileManager.uploadFile(finalLocalFile.getAbsolutePath());
             } else {
                 throw new RuntimeException("Файл Не скачан дальнейшая работа невозможна");
             }

             }else {
                 ftpFileManager.uploadFile(currentFile.getAbsolutePath());
             }
         }//for
          if (ftpConnectionManager.isConnected()){
              ftpConnectionManager.disconnect();
          }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }// end of method uploadTmp

public void deleteAllTmp(Context context, DateTimeManager dateTimeManager){
    String mainFolderName ="WorkClockFiles";
    String currentYear= dateTimeManager.getYear();
    String currentMonthYear= dateTimeManager.getFormattedMonthYear();
    File baseDir = context.getExternalFilesDir(null); // Базовая директория приложения
    File mainF = new File(baseDir, mainFolderName+"/" + currentYear+ "/"+currentMonthYear);
    List<String> relativePaths = new ArrayList<>();
    List<File> files = collectFiles(mainF, "", relativePaths);

}

}//end of Class