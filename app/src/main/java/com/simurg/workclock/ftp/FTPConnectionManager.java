package com.simurg.workclock.ftp;

import android.util.Log;

import com.simurg.workclock.log.FileLogger;

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
            FileLogger.log("connectFtp", "Connect to Ftp "+ host);
            ftpClient.connect(host);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setParserFactory(new DefaultFTPFileEntryParserFactory());

            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                FileLogger.logError("connect FtpContManager", "Error connect. Reply code: "+ replyCode);
                disconnect();
                return false;
            }
            logInfo("Подключение успешно.");
            return true;

        } catch (IOException e) {
            FileLogger.logError("Ftp Connect", "Connection failed "+ e.getMessage()+ "  "+ Log.getStackTraceString(e));
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
            FileLogger.log("loginFtp", "Login "+username);
            boolean success = ftpClient.login(username, password);
            int replyCode = ftpClient.getReplyCode();

            if (!success || !FTPReply.isPositiveCompletion(replyCode)) {
                FileLogger.logError("Login","Login failed. Reply code: "+ replyCode);
                return false;
            }
            logInfo("Авторизация успешна.");
            ftpClient.enterLocalPassiveMode();
            return true;

        } catch (IOException e) {
            FileLogger.logError("Login", "Login failed "+e.getMessage()+"    "+ Log.getStackTraceString(e));
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
            FileLogger.logError("Logout", "Logout failed " +e.getMessage() +"    "+ Log.getStackTraceString(e));
        }
    }

    public void disconnect() {
        try {
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
                logInfo("Отключение от сервера выполнено.");
            }
        } catch (IOException e) {
            FileLogger.logError("Disconnect",  "Disconnect failed "+ e.getMessage()+"    "+ Log.getStackTraceString(e));
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

public boolean reconnect(String hostname, String user, String password){
        if (!isConnected){
            connect(hostname);
       return     login(user,password);
        }
        return true;
}
    public boolean reconnect(){
        if (!isConnected){
            Log.e("Connect", "Go to connect not connected");
            connect(hostname);
            return  login(user,password);
        }
        return true;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }
}


