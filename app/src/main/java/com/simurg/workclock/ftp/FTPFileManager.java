package com.simurg.workclock.ftp;

import android.util.Log;

import com.simurg.workclock.log.FileLogger;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class FTPFileManager {

    private final FTPClient ftpClient;

    public FTPFileManager(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    /**
     * Загрузка файла в текущую рабочую директорию.
     */
    public boolean uploadFile(String localFileName) {
        try (FileInputStream fis = new FileInputStream(new File(localFileName))) {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            String remoteFileName = new File(localFileName).getName(); // Получаем имя файла
            boolean success = ftpClient.storeFile(remoteFileName, fis); // Используем только имя файла
            if (!success) {
                int replyCode = ftpClient.getReplyCode();
                String replyMessage = ftpClient.getReplyString();
                FileLogger.logError("uploadFile", "Upload failed: FileName=" + localFileName
                        + ", ReplyCode=" + replyCode
                        + ", ReplyMessage=" + replyMessage);
            }
            FileLogger.log("uploadFile", "FileName "+ localFileName+ "  Current Dir "+success);
            return success;
        } catch (IOException e) {
            FileLogger.logError("FTPFileManager uploadFile", "Error upload file: " + e.getMessage()+ "  "+ Log.getStackTraceString(e));
            return false;
        }
    }


    /**
     * Скачивание файла из текущей рабочей директории.
     */
    public boolean downloadFile(String remoteFileName, String localFilePath) {
        try (FileOutputStream fos = new FileOutputStream(new File(localFilePath))) {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            boolean success = ftpClient.retrieveFile(remoteFileName, fos);
            logOperation("Download", remoteFileName, localFilePath, success);
            return success;
        } catch (IOException e) {
            FileLogger.logError("FTPFileManager download file", "Error upload file: " + e.getMessage()+ "  "+Log.getStackTraceString(e));
            return false;
        }
    }

    /**
     * Удаление файла в текущей рабочей директории.
     */
    public boolean deleteFile(String fileName) {
        try {
            boolean success = ftpClient.deleteFile(fileName);
            logOperation("Delete", fileName, "Current Directory", success);
            return success;
        } catch (IOException e) {
            System.err.println("Ошибка удаления файла: " + e.getMessage());
            return false;
        }
    }

    /**
     * Создание поддиректории в текущей рабочей директории.
     */
    public boolean createDirectory(String directoryName) {
        try {
            boolean success = ftpClient.makeDirectory(directoryName);
            logOperation("Create Directory", directoryName, "Current Directory", success);
            return success;
        } catch (IOException e) {
            FileLogger.logError("Error mkDir:", e.getMessage()+"  "+Log.getStackTraceString(e) );
            return false;
        }
    }

    /**
     * Смена текущей рабочей директории.
     */
    public boolean changeWorkingDirectory(String remoteDirectoryPath) {
        try {
            boolean success = ftpClient.changeWorkingDirectory(remoteDirectoryPath);
            FileLogger.log("changeWorkingDirectory", "Change to " +remoteDirectoryPath + "  "+ success);
            return success;
        } catch (IOException e) {
            FileLogger.logError("Error change working dir", e.getMessage()+ "   "+Log.getStackTraceString(e));
            return false;
        }
    }

    /**
     * Получение текущей рабочей директории.
     */
    public String getCurrentWorkingDirectory() {
        try {
            return ftpClient.printWorkingDirectory();
        } catch (IOException e) {
            FileLogger.logError("getCurrentWorkingDirectory", "error get current work dir "+ e.getMessage()+ "    "+ Log.getStackTraceString(e));
            return null;
        }
    }



     public void uploadHtmlFile(String localFileName, String subdivision){



     }
    /**
     * Навигация к родительской директории.
     */
    public boolean navigateToParentDirectory() {
        try {
            boolean success = ftpClient.changeToParentDirectory();
           FileLogger.log("navigateToParentDirectory", " navigToParDir"+success);
            return success;
        } catch (IOException e) {
            FileLogger.logError("navigateToParentDirectory", "Error navigateToParDir "+e.getMessage()+"  "+ Log.getStackTraceString(e));
            return false;
        }
    }

    /**
     * Логирование операций.
     */
    private void logOperation(String operation, String source, String target, boolean success) {
        System.out.println(operation + " " +
                (success ? "успешно: " : "неудачно: ") +
                (source != null ? "Source: " + source + " " : "") +
                (target != null ? "Target: " + target : ""));
    }

    /**
     * Обеспечивает существование директории на сервере FTP и переходит в неё.
     *
     * @param ftpFileManager объект FTPFileManager для работы с сервером.
     * @param fullPath полный путь к директории (например, "2024/12.2024").
     * @return true, если успешно перешёл в директорию; false, если возникла ошибка.
     * @throws IOException если произошла ошибка при создании директории.
     */
    public boolean ensureAndChangeToDirectory(FTPFileManager ftpFileManager, String fullPath) throws IOException {
        String[] directories = fullPath.split("/"); // Разбиваем путь на компоненты
        String currentPath = "";

        for (String dir : directories) {
            currentPath += "/" + dir; // Строим текущий путь по мере продвижения

            // Пытаемся перейти в текущую директорию
            boolean changed = ftpFileManager.changeWorkingDirectory(currentPath);

            if (!changed) {
                // Если директория не существует, создаём её
                boolean created = ftpFileManager.createDirectory(currentPath);

                if (!created) {
                    FileLogger.logError("FTPFileManager/ensureAndChangeToDirectory", "Error mkdir "+currentPath );
                    return false;
                }

                // После создания пробуем снова перейти в неё
                ftpFileManager.changeWorkingDirectory(currentPath);
            }
        }
        return true; // Успешно перешли в конечную директорию
    }

public boolean moveCurrentDir(FTPFileManager ftpFileManager, String path) throws IOException {
        if (!Objects.equals(ftpFileManager.getCurrentWorkingDirectory(),path)){
          ftpFileManager.navigateToParentDirectory();
          if (!ensureAndChangeToDirectory(ftpFileManager,path)){
              FileLogger.logError("moveCurrentDir", " Cant change dir "+ path);
              return false;
          }
        }
        FileLogger.log("moveCurrentDir", "Success move to "+ path);
        return  true;
}//end of method
    public boolean fileExists(String fileName) {
        try {
            FTPFile[] files = ftpClient.listFiles(fileName); // Проверяем текущую директорию
            return files.length > 0 && files[0].isFile(); // Если найдено, проверяем, что это файл
        } catch (IOException e) {
            FileLogger.logError("FTPFileManager fileExists", "Error file exists "+ e.getMessage()+ "   "+ Log.getStackTraceString(e) );
            return false;
        }
    }

    public long getFileSize(String fileName, FTPClient ftpClient) throws IOException {
        FTPFile[] files = ftpClient.listFiles(fileName);
        if (files.length == 1) {
            return files[0].getSize();
        }
        return -1; // Файл не найден
    }


}//edn of class
