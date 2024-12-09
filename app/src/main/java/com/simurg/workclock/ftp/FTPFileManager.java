package com.simurg.workclock.ftp;

import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
                Log.e("FTP   LOAD   FILE","Upload failed: FileName=" + localFileName
                        + ", ReplyCode=" + replyCode
                        + ", ReplyMessage=" + replyMessage);
            }
            logOperation("Upload", localFileName, "Current Directory", success);
            return success;
        } catch (IOException e) {
            System.err.println("Ошибка загрузки файла: " + e.getMessage());
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
            System.err.println("Ошибка скачивания файла: " + e.getMessage());
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
            System.err.println("Ошибка создания директории: " + e.getMessage());
            return false;
        }
    }

    /**
     * Смена текущей рабочей директории.
     */
    public boolean changeWorkingDirectory(String remoteDirectoryPath) {
        try {
            boolean success = ftpClient.changeWorkingDirectory(remoteDirectoryPath);
            logOperation("Change Directory", remoteDirectoryPath, null, success);
            return success;
        } catch (IOException e) {
            System.err.println("Ошибка смены рабочей директории: " + e.getMessage());
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
            System.err.println("Ошибка получения текущей рабочей директории: " + e.getMessage());
            return null;
        }
    }

    /**
     * Навигация к родительской директории.
     */
    public boolean navigateToParentDirectory() {
        try {
            boolean success = ftpClient.changeToParentDirectory();
            logOperation("Navigate To Parent Directory", null, null, success);
            return success;
        } catch (IOException e) {
            System.err.println("Ошибка навигации вверх: " + e.getMessage());
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
}
