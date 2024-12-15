package com.simurg.workclock;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
public class FileCollector {

//    /**
//     * Рекурсивно собирает пути всех файлов, начиная с заданной папки.
//     *
//     * @param folder    Начальная папка.
//     * @param basePath  Базовый путь для создания относительных путей.
//     * @param filePaths Список для хранения найденных файлов.
//     */
//    public static void collectFiles(File folder, String basePath, List<String> filePaths) {
//        if (folder == null || !folder.exists()) {
//            return;
//        }
//
//        File[] filesAndDirs = folder.listFiles();
//        if (filesAndDirs == null) {
//            return;
//        }
//
//        for (File fileOrDir : filesAndDirs) {
//            if (fileOrDir.isFile()) {
//                // Формируем полный путь, включая базовый
//                String relativePath = basePath + "/" + fileOrDir.getName();
//                filePaths.add(relativePath);
//            } else if (fileOrDir.isDirectory()) {
//                // Рекурсивно обходим вложенные директории
//                String newBasePath = basePath + "/" + fileOrDir.getName();
//                collectFiles(fileOrDir, newBasePath, filePaths);
//            }
//        }
//    }

    /**
     * Рекурсивно собирает пути всех файлов и возвращает массив файлов.
     *
     * @param folder    Начальная папка.
     * @param basePath  Базовый путь для создания относительных путей.
     * @param filePaths Список для хранения относительных путей файлов.
     * @return Массив файлов.
     */
    public static List<File> collectFiles(File folder, String basePath, List<String> filePaths) {
        List<File> collectedFiles = new ArrayList<>();

        if (folder == null || !folder.exists()) {
            return collectedFiles;
        }

        File[] filesAndDirs = folder.listFiles();
        if (filesAndDirs == null) {
            return collectedFiles;
        }

        for (File fileOrDir : filesAndDirs) {
            if (fileOrDir.isFile()) {
                // Формируем относительный путь и добавляем в список
                String relativePath = basePath + "/" + fileOrDir.getName();
                filePaths.add(relativePath);

                // Добавляем сам файл в результирующий список
                collectedFiles.add(fileOrDir);
            } else if (fileOrDir.isDirectory()) {
                // Рекурсивно обходим вложенные директории
                String newBasePath = basePath + "/" + fileOrDir.getName();
                collectedFiles.addAll(collectFiles(fileOrDir, newBasePath, filePaths));
            }
        }

        return collectedFiles;
    }

    /**
     * Рекурсивно собирает пути всех файлов и возвращает список файлов.
     *
     * @param folder Начальная папка.
     * @return Список файлов.
     */
    public static List<File> collectFiles2(File folder) {
        List<File> collectedFiles = new ArrayList<>();

        if (folder == null || !folder.exists()) {
            return collectedFiles;
        }

        File[] filesAndDirs = folder.listFiles();
        if (filesAndDirs == null) {
            return collectedFiles;
        }

        for (File fileOrDir : filesAndDirs) {
            if (fileOrDir.isFile()) {
                // Добавляем файл в результирующий список
                collectedFiles.add(fileOrDir);
            } else if (fileOrDir.isDirectory()) {
                // Рекурсивно обходим вложенные директории
                collectedFiles.addAll(collectFiles2(fileOrDir));
            }
        }

        return collectedFiles;
    }



}