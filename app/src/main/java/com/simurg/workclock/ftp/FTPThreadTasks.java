package com.simurg.workclock.ftp;

import static com.simurg.workclock.FileCollector.collectFiles;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.simurg.workclock.RFIDHandler;
import com.simurg.workclock.data.DateTimeManager;
import com.simurg.workclock.file.CsvReader;
import com.simurg.workclock.file.DataQueueManager;
import com.simurg.workclock.file.FileManagerDesktop;
import com.simurg.workclock.log.FileLogger;
import com.simurg.workclock.log.LogCatToFile;
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
import java.util.concurrent.ScheduledExecutorService;

public class FTPThreadTasks {
    public static synchronized void checkCardFileModify(Context context, CsvReader csvReader) {

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
        FileLogger.log(TAG, "Starting checking card");

        // Получаем информацию о файле на сервере
        FTPFile[] files = ftpConnectionManager.getFtpClient().listFiles(remoteFileName);
        replyCode = ftpConnectionManager.getFtpClient().getReplyCode();
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            FileLogger.logError(TAG,"Cant get list of files " +replyCode);
            return;
        } else {
            FileLogger.log(TAG," List of files gotten Reply code: "+replyCode);
        }

        if (files.length > 0) {
            FileLogger.log(TAG, "File found");
            long remoteFileSize = files[0].getSize();

            // Локальная директория и файл
            File localFolder = new File(context.getExternalFilesDir(null), localFolderName);
            if (!localFolder.exists() && !localFolder.mkdirs()) {
                FileLogger.logError(TAG,"Cant mkdir LOCAL "+localFolder.getAbsolutePath());
                return;
            }

            File localFile = new File(localFolder, "cards.csv");

            // Проверяем наличие локального файла и его размер
            if (localFile.exists() && remoteFileSize != localFile.length()) {
                FileLogger.log(TAG,"The local file is different from the file on the server. Replacing ");
                csvReader.startUpdate();
                try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(localFile))) {
                    if (ftpConnectionManager.getFtpClient().retrieveFile(remoteFileName, outputStream)) {
                        FileLogger.log(TAG,"File updating success "+ localFile.getAbsolutePath());
                        csvReader.finishUpdate();
                    } else {
                        csvReader.finishUpdate();
                        FileLogger.logError(TAG,"Error update card. Reply code: "+ ftpConnectionManager.getFtpClient().getReplyCode());
                    }
                }
            } else if (!localFile.exists()) {
                FileLogger.log(TAG, "No local file, download");
                csvReader.startUpdate();
                try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(localFile))) {
                    if (ftpConnectionManager.getFtpClient().retrieveFile(remoteFileName, outputStream)) {
                        FileLogger.log(TAG,"Download successfully " +localFile.getAbsolutePath());
                        csvReader.finishUpdate();
                    } else {
                        FileLogger.logError(TAG, "Download failed. Reply: "+ ftpConnectionManager.getFtpClient().getReplyCode());
                        csvReader.finishUpdate();
                    }
                }
            } else {
                FileLogger.log(TAG, "Local file==remoteFile. OK");
            }
        } else {
            FileLogger.logError(TAG, "No server file "+ remoteFileName);
        }
    } catch (IOException e) {
        FileLogger.logError(TAG, "Error checking card or download "+ e.getMessage()+"   "+ Log.getStackTraceString(e));
        if (csvReader.checkIsUpdating()){csvReader.finishUpdate();}
    }finally {
        ftpConnectionManager.logout();
        ftpConnectionManager.disconnect();
        Log.i(TAG, "Соединение с FTP-сервером закрыто.");
        if (csvReader.checkIsUpdating()){csvReader.finishUpdate();}

    }
 }

    public static synchronized void sendFileToFtp(Context context, String localFolderName, DateTimeManager dateTimeManager) throws InterruptedException {
        if (NetworkUtils.isNetworkConnected(context)) {
            new Thread(() -> {
                FTPConnectionManager ftpConnectionManager = new FTPConnectionManager();
                FTPFileManager ftpFileManager =new FTPFileManager(ftpConnectionManager.getFtpClient());
                try {
                     File idFile= new File(context.getExternalFilesDir(null),localFolderName+"/id.txt");
                     if (!idFile.exists()){
                         FileLogger.logError("sendFileToFTP", "Id TXT не существует");
                         return;
                     }
                    String fileName= FileManagerDesktop.readFileContent(context,localFolderName,"id.txt")+".txt";
                    String fileContent= dateTimeManager.getFormattedTime()+" "+dateTimeManager.getFormattedDate();
                    File localFile = FileManagerDesktop.createFile(context,localFolderName,fileName,fileContent);
                    if (localFile == null || !localFile.exists()) {
                        FileLogger.logError("sendFileToFtp"," File not created    "+(localFile != null ? localFile.getAbsolutePath() : "null") );
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
                    ftpFileManager.uploadFile(LogCatToFile.getLogFilePath(context));
                     LogCatToFile.checkAndDeleteLogFile(LogCatToFile.getLogFilePath(context),context);
                     if (!FileLogger.isLogExist()) FileLogger.init(context);
                    boolean isUpload=ftpFileManager.uploadFile(FileLogger.getLogFilePath()) ;
                     if (FileLogger.checkLogOverflow()&& isUpload){
                             FileLogger.deleteLogFile(context);}
                    if (uploadSuccess) {
                        Log.i("sendFileToFtp", "Файл успешно загружен: " + localFile.getName());
                    } else {
                        FileLogger.logError("sendFileToFTP", "Error uploadFile "+ localFile.getName());
                    }
                } catch (Exception e) {
                    FileLogger.logError("sendToFTP"," Error Ftp "+ e.getMessage()+"     "+Log.getStackTraceString(e));
                } finally {
                    ftpConnectionManager.logout();
                    ftpConnectionManager.disconnect();
                }
            }).start();

        }else {
            FileLogger.logError("SendFileTOftp", "No INTERNET");
        }
    }
//TODO: Обязательно обработать случай, когда нет временных файлов, т.е. List<File> files пуст(==null)
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
        if(relativePaths.isEmpty()|| files.isEmpty()){
            Log.i("uploadAllTmp", "Нет временных файлов");
            return;
        }
        boolean allFilesUploaded = true;
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
              ftpConnectionManager.logout();
              ftpConnectionManager.disconnect();
          }
        } catch (IOException e) {
            allFilesUploaded = false;
            throw new RuntimeException(e);
        }
        if (allFilesUploaded){
          //  FileManagerDesktop.deleteAllTmp(context,dateTimeManager);
        }
    }// end of method uploadTmp

    public static boolean uploadErrorFile(Context context, DateTimeManager dateTimeManager) {
        FTPConnectionManager ftpConnectionManager = new FTPConnectionManager();
        FTPFileManager ftpFileManager = new FTPFileManager(ftpConnectionManager.getFtpClient());
        String mainFolderName = "WorkClockFiles";
        String currentYear = dateTimeManager.getYear();
        String currentMonthYear = dateTimeManager.getFormattedMonthYear();
        String ftpMonthDir = currentYear + "/" + currentMonthYear + "/";
        File baseDir = context.getExternalFilesDir(null);
        File mainFolder = new File(baseDir, mainFolderName);
        File errorFile = new File(mainFolder, "error.txt");
        String fileContent;
        //String fileContent=FileManagerDesktop.readFileContenFromFile(errorFile);
        File errorFolder = new File(mainFolder, "ErrorFolder");
        File localErrorFile = new File(errorFolder, "error.txt");
if (!errorFile.exists()|| errorFile.length()==0){
    FileLogger.logError("uploadErrorFile", "Desktop Error File not existed");
    return false;
}
        if (!errorFolder.exists()) {
            FileManagerDesktop.createCustomFolder(mainFolder, errorFolder.getName());
        }

        try {
            ftpConnectionManager.connect(FTPConnectionManager.hostname);
            ftpConnectionManager.login(FTPConnectionManager.user, FTPConnectionManager.password);
            ftpFileManager.moveCurrentDir(ftpFileManager, ftpMonthDir);

            boolean success = false;

            while (true) {
                if (ftpFileManager.fileExists(errorFile.getName())) {
                    fileContent=FileManagerDesktop.readFileContenFromFile(errorFile);
                    // Шаг 1: Скачать файл и зафиксировать размер
                    ftpFileManager.downloadFile(errorFile.getName(), localErrorFile.getAbsolutePath());
                    long remoteFileSizeBefore = localErrorFile.length();

                    // Шаг 2: Локально объединить содержимое
                    FileManagerDesktop.writeToFile(localErrorFile, "\n" + fileContent);

                    // Шаг 3: Проверить, изменился ли файл на сервере
                    long remoteFileSizeAfter = ftpFileManager.getFileSize(errorFile.getName(),ftpConnectionManager.getFtpClient());
                    if (remoteFileSizeAfter != remoteFileSizeBefore) {
                        // Если файл изменился, повторяем загрузку
                        continue; // Вернуться к началу цикла для повторной загрузки
                    }

                    // Шаг 4: Загрузить обновленный файл
                    success = ftpFileManager.uploadFile(localErrorFile.getAbsolutePath());
                } else {
                    // Если файл не существует, загружаем напрямую
                    success = ftpFileManager.uploadFile(errorFile.getAbsolutePath());
                }

                break; // Успешно завершили, выходим из цикла
            }
            if (success){
                FileManagerDesktop.deleteFile(errorFile);
                FileManagerDesktop.deleteFile(localErrorFile);
            }
            return success;

        } catch (Exception e) {
            FileLogger.logError("uploadErrorFile", "Error upload file  "+ e.getMessage()+ "     "+Log.getStackTraceString(e));
           if (localErrorFile.exists()){FileManagerDesktop.deleteFile(localErrorFile);}
           return false;
           //OR EXCEPTION
            //throw new RuntimeException("Ошибка при загрузке файла: " + e.getMessage(), e);
        } finally {
            ftpConnectionManager.logout();
            ftpConnectionManager.disconnect();
        }
    }

    public static void uploadAllTmpWithValidation(Context context, DateTimeManager dateTimeManager) {
        FTPConnectionManager ftpConnectionManager = new FTPConnectionManager();
        FTPFileManager ftpFileManager = new FTPFileManager(ftpConnectionManager.getFtpClient());
        String mainFolderName = "WorkClockFiles";
        String currentYear = dateTimeManager.getYear();
        String currentMonthYear = dateTimeManager.getFormattedMonthYear();
        String ftpMonthDir = currentYear + "/" + currentMonthYear;
        File baseDir = context.getExternalFilesDir(null); // Базовая директория приложения
        File mainF = new File(baseDir, mainFolderName + "/" + currentYear + "/" + currentMonthYear);
        List<String> relativePaths = new ArrayList<>(); // относительные пути файлов
        List<File> files = collectFiles(mainF, "", relativePaths);

        if (relativePaths.isEmpty() || files.isEmpty()) {
            FileLogger.log("uploadAllTmp", "no tmp files");
            return;
        }
        boolean allFilesUploaded = true;

        try {
            ftpConnectionManager.connect(FTPConnectionManager.hostname);
            ftpConnectionManager.login(FTPConnectionManager.user, FTPConnectionManager.password);

            for (int i = 0; i < files.size(); i++) {
                String relativePath = relativePaths.get(i);
                String fullPath = ftpMonthDir + relativePath.substring(0, relativePath.lastIndexOf("/"));
                File currentFile = files.get(i);
                String fileName = currentFile.getName();

                ftpFileManager.moveCurrentDir(ftpFileManager, fullPath);

                if (ftpFileManager.fileExists(fileName)) {
                    String localFileName = fileName.substring(0, fileName.indexOf(".")) + "local.html";
                    File localFile = new File(currentFile.getParent(), localFileName);
                    File mergedFile = new File(currentFile.getParent(), fileName);

                    FileManagerDesktop.renameFile(currentFile.getAbsolutePath(), localFileName);

                    // Первый скачанный размер файла
                    long initialServerFileSize = ftpFileManager.getFileSize(fileName,ftpConnectionManager.getFtpClient());
                    ftpFileManager.downloadFile(fileName, mergedFile.getAbsolutePath());
                    HtmlEditor.mergeFiles(context, localFile, mergedFile);
                    while (true) {
                        long currentServerFileSize = ftpFileManager.getFileSize(fileName,ftpConnectionManager.getFtpClient());
                        if (currentServerFileSize != initialServerFileSize) {
                            // Серверный файл изменился, повторяем скачивание и склейку
                            ftpFileManager.downloadFile(fileName, mergedFile.getAbsolutePath());
                            HtmlEditor.mergeFiles(context, localFile, mergedFile);
                            initialServerFileSize = currentServerFileSize; // Обновляем эталонный вес
                        } else {
                            // Серверный файл не изменился, загружаем обновленный файл
                            ftpFileManager.uploadFile(mergedFile.getAbsolutePath());
                            break;
                        }
                    }

                } else {
                    // Файл отсутствует на сервере, загружаем
                    ftpFileManager.uploadFile(currentFile.getAbsolutePath());
                }
            }

        } catch (IOException e) {
            boolean isRenamed= FileManagerDesktop.renameAllTmpWithReplace(new File(baseDir,mainFolderName),dateTimeManager);
            FileLogger.logError("uploadAllTmpWithValid", "IsRenamed:  "+isRenamed+"        "+e.getMessage()+ "     "+ Log.getStackTraceString(e));
            return;
           // throw new RuntimeException(e);
        }finally {
            if (ftpConnectionManager.isConnected()) {
                ftpConnectionManager.logout();
                ftpConnectionManager.disconnect();
            }
        }

        FileLogger.log("uploadAllTmpWithValid", "deleteAllTmp call");
        FileManagerDesktop.deleteAllTmp(context, dateTimeManager);
    }

    public static Runnable cardTask(Activity activity, Context context, CsvReader csvReader) {
        return () -> {
            if (NetworkUtils.isNetworkConnected(context)){
              //  new Thread(() -> {
                    checkCardFileModify(context,csvReader);
              //  }).start();

            }else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FileLogger.logError("cardTask", "NO INTERNET, uiThread");
                        Toast.makeText(context, "Проверка файла карточек невозможна так как интернет отсутствует, повтор через 2 минуты",Toast.LENGTH_LONG).show();

                    }
                });
                FileLogger.logError("cardTask", "NO INTERNET");
              //  throw new RuntimeException("Нет интернета");
                }

        };
    }
    public static Runnable uploadTmpAndErrorTask(Activity activity, Context context, DateTimeManager dateTimeManager, DataQueueManager dataQueueManager, RFIDHandler rfidHandler, CsvReader csvReader, String mainFolderName, File mainFolder ) {
        return () -> {
            if (NetworkUtils.isNetworkConnected(context)){
            //    new Thread(()->{
                    try {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                FileLogger.log("uploadTmpAndErrorTask", "sync start, uiThread");
                                Toast.makeText(context,"НАЧАТА СИНХРОНИЗАЦИЯ С СЕРВЕРОМ, НЕ ОТКЛЮЧАЙТЕ ПРИЛОЖЕНИЕ", Toast.LENGTH_LONG).show();
                            }
                        });
                        dataQueueManager.startSync();
                        uploadAllTmpWithValidation(context,dateTimeManager);
                        uploadErrorFile(context,dateTimeManager);
                    } catch (RuntimeException e) {
                        FileLogger.logError("uploadTmpAndErrorTask", "Exception   "+ e.getMessage()+"    "+ Log.getStackTraceString(e));
                        //throw new RuntimeException(e);
                    }finally {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                FileLogger.log("uploadTmpAndErrorTask", "sync end, 3 min repeat, ui thread");
                                Toast.makeText(context,"СИНХРОНИЗАЦИЯ ЗАВЕРШЕНА, ПОВТОР ЧЕРЕЗ 3 МИНУТЫ ", Toast.LENGTH_LONG).show();
                            }
                        });
                        dataQueueManager.finishSyncAndProcessQueue(rfidHandler,activity,dateTimeManager,mainFolderName,mainFolder,csvReader);
                    }
              //  }).start();

            }else {
                FileLogger.logError("uploadTmpAndErrorTask", "NO INTERNET ");
               activity.runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       FileLogger.logError("uploadTmpAndErrorTask", "NO INTERNET, uiThread ");
                       Toast.makeText(context,"Синхронизация невозможна так как интернет отсутствует, повтор через 3 минуты",Toast.LENGTH_LONG).show();

                   }
               });
               // throw new RuntimeException("Нет интернета");
            }
            };

    }


    public static void testFunction(Context context, ScheduledExecutorService mainSheduller){
        if (mainSheduller.isShutdown()|| mainSheduller.isTerminated()){
            Log.e("()()()()()()()()()()()()()()", "()()()()()()()()(()()()()()()Планировщик мертв");
        }
        if (!NetworkUtils.isNetworkConnected(context)){
            Log.e(" IF Test1","---------------------------------------- Интернета нет");
        }else {
            Log.w("ELSE Test1", "+++++++++++++++++++++++++++++++++ ПРОДОЛЖАЕМ РАБОТУ");
        }
    }


    public static Runnable testFunction2(Context context){
return ()->{
    if (!NetworkUtils.isNetworkConnected(context)){
        Log.e(" IF Test2","---------------------------------------- Интернета нет");
    }else {
        Log.w("ELSE Test2", "+++++++++++++++++++++++++++++++++ ПРОДОЛЖАЕМ РАБОТУ");
    }
};
}


}//end of Class
