package com.simurg.workclock.ftp;

import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;

import org.apache.commons.net.ftp.parser.DefaultFTPFileEntryParserFactory;

public class FTPConnectionManager {

    private FTPClient ftpClient;
    private static final String TAG = "FTPConnectionManager";
    public static final String hostname = "ftp.simurg.by";
    public static final String user = "timetracker@timetracker.simurg-mp.com";
    public static final String password = "TimetrackerAdmin";
    private boolean isConnected;
    public FTPConnectionManager() {
        ftpClient = new FTPClient();
    }

    public boolean connect(String host) {
        try {
            logInfo("Подключение к серверу: " + host + ":21");
            ftpClient.connect(host);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setParserFactory(new DefaultFTPFileEntryParserFactory());

            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                logError("Ошибка подключения. Код ответа: " + replyCode);
                disconnect();
                return false;
            }

            logInfo("Подключение успешно.");
            return true;

        } catch (IOException e) {
            logError("Ошибка подключения: " + e.getMessage());
            return false;
        }
    }

//    public synchronized FTPClient getConnection() throws IOException {
//        if (this.getFtpClient() == null || !this.getFtpClient().isConnected()) {
//            this.getFtpClient().connect(hostname);
//            this.getFtpClient().login(user, password);
//            this.getFtpClient().enterLocalPassiveMode();
//            isConnected = true;
//        }
//        return ftpClient;
//    }

    public boolean login(String username, String password) {
        try {
            logInfo("Авторизация пользователя: " + username);
            boolean success = ftpClient.login(username, password);
            int replyCode = ftpClient.getReplyCode();

            if (!success || !FTPReply.isPositiveCompletion(replyCode)) {
                logError("Ошибка авторизации. Код ответа: " + replyCode);
                return false;
            }

            logInfo("Авторизация успешна.");
            ftpClient.enterLocalPassiveMode();
            return true;

        } catch (IOException e) {
            logError("Ошибка авторизации: " + e.getMessage());
            return false;
        }
    }

    public void logout() {
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                logInfo("Выход из учетной записи выполнен.");
            }
        } catch (IOException e) {
            logError("Ошибка при выходе из учетной записи: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
                logInfo("Отключение от сервера выполнено.");
            }
        } catch (IOException e) {
            logError("Ошибка при отключении от сервера: " + e.getMessage());
        }
    }


//    public synchronized void disconnect() throws IOException {
//        if (this.ftpClient != null && this.ftpClient.isConnected()) {
//            this.ftpClient.logout();
//            this.ftpClient.disconnect();
//            isConnected = false;
//        }
//    }

    public boolean isConnected() {
        return ftpClient.isConnected();
    }

    private void logInfo(String message) {
        Log.i(TAG , " [INFO]: " + message);
    }

    private void logError(String message) {
        Log.e(TAG , " [ERROR]: " + message);
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }
}


