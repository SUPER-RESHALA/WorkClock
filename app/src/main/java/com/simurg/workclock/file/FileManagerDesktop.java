package com.simurg.workclock.file;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManagerDesktop {

    public static File createCustomFolder(Context context, String customFolderName) {
        File baseDir = context.getExternalFilesDir(null); // Базовая директория приложения
        if (baseDir == null) {
            Log.e("FileManager", "Не удалось получить доступ к внешнему хранилищу.");
            return null;
        }

        File customFolder = new File(baseDir, customFolderName); // Папка с указанным именем

        if (!customFolder.exists()) {
            boolean created = customFolder.mkdirs(); // Создание папки
            if (created) {
                Log.i("FileManager", "Папка успешно создана: " + customFolder.getAbsolutePath());
            } else {
                Log.e("FileManager", "Не удалось создать папку: " + customFolder.getAbsolutePath());
            }
        } else {
            Log.i("FileManager", "Папка уже существует: " + customFolder.getAbsolutePath());
        }

        return customFolder;
    }


    public static boolean createFile(Context context, String folderName, String fileName, String fileContent) {
        File folder = new File(context.getExternalFilesDir(null), folderName);
        if (!folder.exists()) {
            Log.e("FileManager", "Папка не существует: " + folder.getAbsolutePath());
            return false;
        }

        File file = new File(folder, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(fileContent.getBytes());
            fos.flush();
            Log.i("FileManager", "Файл успешно создан: " + file.getAbsolutePath());
            return true;
        } catch (IOException e) {
            Log.e("FileManager", "Ошибка при создании файла: " + e.getMessage(), e);
            return false;
        }
    }





    public static File createFileNEWMETHOD(Context context, String folderName, String fileName, String fileContent) {
        File folder = new File(context.getExternalFilesDir(null), folderName);

        // Проверяем существование папки, при необходимости создаем
        if (!folder.exists() && !folder.mkdirs()) {
            Log.e("FileManager", "Не удалось создать папку: " + folder.getAbsolutePath());
            return null; // Возвращаем null, так как файл создать невозможно
        }

        File file = new File(folder, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(fileContent.getBytes());
            fos.flush();
            Log.i("FileManager", "Файл успешно создан: " + file.getAbsolutePath());
            return file;
        } catch (IOException e) {
            Log.e("FileManager", "Ошибка при создании файла: " + e.getMessage(), e);
            return null; // Возвращаем null в случае ошибки
        }
    }






    public static List<File> getAllFoldersInDirectory(Context context, String parentDirectoryName) {
        File parentDirectory = new File(context.getExternalFilesDir(null), parentDirectoryName);
        List<File> foldersList = new ArrayList<>();

        // Проверяем, существует ли родительская папка
        if (parentDirectory.exists() && parentDirectory.isDirectory()) {
            // Получаем список всех файлов в директории
            File[] files = parentDirectory.listFiles();

            if (files != null) {
                // Перебираем все файлы и добавляем только те, которые являются папками
                for (File file : files) {
                    if (file.isDirectory()) {
                        foldersList.add(file);
                    }
                }
            }
        } else {
            Log.e("FileManager", "Директория не существует или не является директорией: " + parentDirectory.getAbsolutePath());
        }

        return foldersList;
    }

    public static String readFileContent(Context context, String folderName, String fileName) {
        File folder = new File(context.getExternalFilesDir(null), folderName);
        File file = new File(folder, fileName);

        if (!file.exists()) {
            Log.e("FileReader", "Файл не найден: " + file.getAbsolutePath());
            return null;
        }

        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            Log.e("FileReader", "Ошибка при чтении файла: " + e.getMessage(), e);
            return null;
        }

        return content.toString().trim(); // Возвращаем содержимое файла без лишних переносов строк в конце
    }

    public static List<File> getListAllFoldersInExternalFilesDir(Context context) {
        List<File> folderList = new ArrayList<>();

        // Получаем базовую директорию
        File parentDirectory = context.getExternalFilesDir(null);

        // Проверяем, что директория существует и доступна
        if (parentDirectory == null) {
            Log.e("FileManager", "Базовая директория внешнего хранилища недоступна.");
            return folderList;
        }

        // Получаем все файлы и папки в текущей директории
        File[] files = parentDirectory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Если это папка, добавляем ее в список
                    folderList.add(file);
                }
            }
        }

        return folderList;
    }

}
