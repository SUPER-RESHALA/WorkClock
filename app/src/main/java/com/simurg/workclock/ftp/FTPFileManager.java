package com.simurg.workclock.ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

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
     * Загрузка файла на сервер.
     */
    public boolean uploadFile(String localFilePath, String remoteFilePath) {
        try (FileInputStream fis = new FileInputStream(new File(localFilePath))) {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            boolean success = ftpClient.storeFile(remoteFilePath, fis);
            logOperation("Upload", localFilePath, remoteFilePath, success);
            return success;
        } catch (IOException e) {
            System.err.println("Ошибка загрузки файла: " + e.getMessage());
            return false;
        }
    }

    /**
     * Скачивание файла с сервера.
     */
    public boolean downloadFile(String remoteFilePath, String localFilePath) {
        try (FileOutputStream fos = new FileOutputStream(new File(localFilePath))) {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            boolean success = ftpClient.retrieveFile(remoteFilePath, fos);
            logOperation("Download", remoteFilePath, localFilePath, success);
            return success;
        } catch (IOException e) {
            System.err.println("Ошибка скачивания файла: " + e.getMessage());
            return false;
        }
    }

    /**
     * Удаление файла на сервере.
     */
    public boolean deleteFile(String remoteFilePath) {
        try {
            boolean success = ftpClient.deleteFile(remoteFilePath);
            logOperation("Delete", remoteFilePath, null, success);
            return success;
        } catch (IOException e) {
            System.err.println("Ошибка удаления файла: " + e.getMessage());
            return false;
        }
    }

    /**
     * Создание директории на сервере.
     */
    public boolean createDirectory(String remoteDirectoryPath) {
        try {
            boolean success = ftpClient.makeDirectory(remoteDirectoryPath);
            logOperation("Create Directory", remoteDirectoryPath, null, success);
            return success;
        } catch (IOException e) {
            System.err.println("Ошибка создания директории: " + e.getMessage());
            return false;
        }
    }

    /**
     * Проверка наличия изменений файла на сервере.
     */
    public boolean isFileModified(String remoteFilePath, long localLastModifiedTime) {
        try {
            FTPFile[] files = ftpClient.listFiles(remoteFilePath);
            if (files.length == 1) {
                long remoteTimestamp = files[0].getTimestamp().getTimeInMillis();
                return remoteTimestamp > localLastModifiedTime;
            }
        } catch (IOException e) {
            System.err.println("Ошибка проверки файла: " + e.getMessage());
        }
        return false;
    }

    /**
     * Список файлов в директории.
     */
    public FTPFile[] listFiles(String remoteDirectoryPath) {
        try {
            return ftpClient.listFiles(remoteDirectoryPath);
        } catch (IOException e) {
            System.err.println("Ошибка получения списка файлов: " + e.getMessage());
            return new FTPFile[0];
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
