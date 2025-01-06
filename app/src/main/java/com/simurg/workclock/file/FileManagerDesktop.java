package com.simurg.workclock.file;

import static com.simurg.workclock.FileCollector.collectFiles;

import android.content.Context;
import android.util.Log;

import com.simurg.workclock.FileCollector;
import com.simurg.workclock.data.DateTimeManager;
import com.simurg.workclock.entity.Employee;
import com.simurg.workclock.template.HtmlEditor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public static File createCustomFolder(File parentFolder, String customFolderName) {
        if (parentFolder == null || !parentFolder.exists()) {
            Log.e("FileManager", "Базовая папка не существует или не задана.");
            return null;
        }

        File customFolder = new File(parentFolder, customFolderName); // Создаём папку внутри указанной родительской

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


    public static boolean createFileBoolean(Context context, String folderName, String fileName, String fileContent) {
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





    public static File createFile(Context context, String folderName, String fileName, String fileContent) {
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

    public static File createFileInCustomFolder(File parentFolder, String fileName, String fileContent) {
        // Проверяем, существует ли родительская папка, если нет - создаем
        if (parentFolder != null && !parentFolder.exists() && !parentFolder.mkdirs()) {
            Log.e("FileManager", "Не удалось создать папку: " + parentFolder.getAbsolutePath());
            return null; // Если папка не может быть создана, возвращаем null
        }

        // Создаем файл внутри родительской папки
        File file = new File(parentFolder, fileName);
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


    public static String readFileContenFromFile(File file) {
        StringBuilder fileContent = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }
        } catch (IOException e) {
            Log.e("FileManager", "Ошибка при чтении файла: " + e.getMessage(), e);
            return null; // Возвращаем null в случае ошибки
        }

        return fileContent.toString().trim(); // Возвращаем содержимое файла без лишних пробелов
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

    public static File writeToFile(File outputFile, String finalContent) {
        // Проверяем, существует ли файл, если нет, то создаем его
        try {
            if (outputFile != null) {
                // Получаем родительскую папку файла и создаем ее, если она не существует
                File parentFolder = outputFile.getParentFile();
                if (parentFolder != null && !parentFolder.exists()) {
                    boolean dirsCreated = parentFolder.mkdirs(); // Создаем все недостающие папки
                    if (!dirsCreated) {
                        Log.e("FileManager", "Не удалось создать папки: " + parentFolder.getAbsolutePath());
                    }
                }

                // Создаем новый файл, если он не существует
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                }

                // Записываем содержимое в файл
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) {
                    writer.write(finalContent);
                    writer.flush();  // Очищаем буфер
                    Log.i("FileManager", "Содержимое успешно записано в файл: " + outputFile.getAbsolutePath());
                    return outputFile;  // Возвращаем объект File
                }
            }
        } catch (IOException e) {
            Log.e("FileManager", "Ошибка при записи в файл: " + e.getMessage(), e);
        }
        return null;  // Возвращаем null в случае ошибки
    }



    public static String[] splitString(String input, String delimiter) {
        if (input == null || delimiter == null) {
            throw new IllegalArgumentException("Input string and delimiter cannot be null");
        }
        return input.split("\\Q" + delimiter + "\\E"); // \Q и \E экранируют разделитель
    }



    public static File createTemplateFileForQueue(Context context, Employee employee, String mainFolderName, DateTimeManager dateTimeManager, File mainFolder, String dateAndTime){
        String[] workerInfo = splitString(dateAndTime, "|");
        String currentYear=dateTimeManager.getYear();//+++++
        String currentDate=workerInfo[2];
        String currentMonthYear= dateTimeManager.getFormattedMonthYear();//+++++
        String currentTime= workerInfo[1];
        String subdivision= employee.getSubdivision();
        String code =employee.getCode();
        //String note= FileManagerDesktop.readFileContent(context,mainFolder.getName(),"id.txt");
        String note= FileManagerDesktop.readFileContent(context,mainFolderName,"id.txt");
        File yearFolder=  createCustomFolder(mainFolder,currentYear);
        File monthYearFolder= createCustomFolder(yearFolder,currentMonthYear);
        File subdivisionFolder = createCustomFolder(monthYearFolder,subdivision);
        File htmlFile= new File(subdivisionFolder,code+".html");
        HtmlEditor htmlEditor = new HtmlEditor(context,"template.html");
        if (!htmlFile.exists()){
            String template= htmlEditor.loadTemplate();
            String finalContent = htmlEditor.addNewRowToHtml(template,code,currentDate,currentTime,note);
            htmlFile = createFileInCustomFolder(subdivisionFolder,htmlFile.getName(),finalContent);
        }else {
            String htmlFileContent= readFileContenFromFile(htmlFile);
            //System.out.println(htmlFileContent);
            String finalContent= htmlEditor.addNewRowToHtml(htmlFileContent,code,currentDate,currentTime,note);
            htmlFile = createFileInCustomFolder(subdivisionFolder,htmlFile.getName(),finalContent);
        }
        return htmlFile;
    }


    public static File createTemplateFile(Context context, Employee employee, String mainFolderName, DateTimeManager dateTimeManager, File mainFolder){
    String currentYear=dateTimeManager.getYear();
    String currentDate=dateTimeManager.getFormattedDate();
    String currentMonthYear= dateTimeManager.getFormattedMonthYear();
    String currentTime= dateTimeManager.getFormattedTime();
    String subdivision= employee.getSubdivision();
    String code =employee.getCode();
        //String note= FileManagerDesktop.readFileContent(context,mainFolder.getName(),"id.txt");
    String note= FileManagerDesktop.readFileContent(context,mainFolderName,"id.txt");
      File yearFolder=  createCustomFolder(mainFolder,currentYear);
      File monthYearFolder= createCustomFolder(yearFolder,currentMonthYear);
      File subdivisionFolder = createCustomFolder(monthYearFolder,subdivision);
      File htmlFile= new File(subdivisionFolder,code+".html");
     HtmlEditor htmlEditor = new HtmlEditor(context,"template.html");
      if (!htmlFile.exists()){
         String template= htmlEditor.loadTemplate();
         String finalContent = htmlEditor.addNewRowToHtml(template,code,currentDate,currentTime,note);
         htmlFile = createFileInCustomFolder(subdivisionFolder,htmlFile.getName(),finalContent);
      }else {
         String htmlFileContent= readFileContenFromFile(htmlFile);
         String finalContent= htmlEditor.addNewRowToHtml(htmlFileContent,code,currentDate,currentTime,note);
          htmlFile = createFileInCustomFolder(subdivisionFolder,htmlFile.getName(),finalContent);
      }
     return htmlFile;
 }

    /**
     * Переименовывает локальный файл.
     *
     * @param originalFilePath Полный путь к исходному файлу.
     * @param newFileName      Новое имя файла (без пути).
     * @return {@code true}, если файл был успешно переименован; {@code false} в противном случае.
     */
    public static boolean renameFile(String originalFilePath, String newFileName) {
        File originalFile = new File(originalFilePath);

        // Проверяем, существует ли исходный файл
        if (!originalFile.exists()) {
            Log.e("renameFile","Файл не найден: " + originalFilePath);
            return false;
        }

        // Создаем новый объект File с новым именем в той же директории
        File newFile = new File(originalFile.getParent(), newFileName);

        // Проверяем, не существует ли уже файл с новым именем
        if (newFile.exists()) {
           Log.e("renameFile","Файл с именем " + newFile.getAbsolutePath() + " уже существует.");
            return false;
        }

        // Переименовываем файл
        boolean success = originalFile.renameTo(newFile);
        if (success) {
            System.out.println("Файл успешно переименован: " + newFile.getAbsolutePath());
        } else {
            Log.e("renameFile","Не удалось переименовать файл: " + originalFilePath);
        }

        return success;
    }

    public static boolean deleteFile(File file) {
        if (file != null && file.isFile()) {
            return file.delete(); // Удаляет файл, если это действительно файл
        }
        return false; // Если это не файл, возвращаем false
    }
    public static void deleteAllTmp(Context context, DateTimeManager dateTimeManager){
        String mainFolderName ="WorkClockFiles";
        String currentYear= dateTimeManager.getYear();
        String currentMonthYear= dateTimeManager.getFormattedMonthYear();
        File baseDir = context.getExternalFilesDir(null); // Базовая директория приложения
        File mainF = new File(baseDir, mainFolderName+"/" + currentYear+ "/"+currentMonthYear);
        List<String> relativePaths = new ArrayList<>();
        List<File> files = collectFiles(mainF, "", relativePaths);
        if (files.isEmpty()){
            return;
        }
     for (int i=0; i< files.size();i++){
        deleteFile(files.get(i));
     }
    }


    public static  boolean delFinalFiles(File mainFolder, DateTimeManager dateTimeManager){
        String currentYear= dateTimeManager.getYear()+"/";
        String currentMonthYear= dateTimeManager.getFormattedMonthYear()+"/";
        String path= currentYear+currentMonthYear;
        File mainTmpFolder= new File(mainFolder, path);
        List<File> allTmpFiles= FileCollector.collectOnlyFiles(mainTmpFolder);
        if (allTmpFiles.isEmpty()){
            return false;
        }
        for (File file: allTmpFiles) {
            if (!file.getName().contains("local")){
               if (!FileManagerDesktop.deleteFile(file)){
                   return false;
               }
            }
        }
        return true;
    }


    public static boolean renameAllTmp(File mainFolder, DateTimeManager dateTimeManager) {
        String currentYear = dateTimeManager.getYear() + "/";
        String currentMonthYear = dateTimeManager.getFormattedMonthYear() + "/";
        String path = currentYear + currentMonthYear;
        File mainTmpFolder = new File(mainFolder, path);

        // Удаляем ненужные файлы перед переименованием
        if (!delFinalFiles(mainFolder, dateTimeManager)) {
            Log.e("renameAllTmp", "Ошибка при удалении временных файлов.");
            return false;
        }

        // Сбор всех файлов
        List<File> allTmpFiles = FileCollector.collectOnlyFiles(mainTmpFolder);
        boolean allRenamedSuccessfully = true;
if (allTmpFiles.isEmpty()){
    return false;
}
        for (File file : allTmpFiles) {
            if (file.getName().endsWith("local.html")) {
                String newName = file.getName().replace("local.html", ".html");
                // File renamedFile = new File(file.getParent(), newName);
               boolean success= FileManagerDesktop.renameFile(file.getAbsolutePath(),newName);
               // boolean success = file.renameTo(renamedFile);
                if (success) {
                    Log.i("renameAllTmp", "Файл " + file.getName() + " переименован в " + newName);
                } else {
                    Log.e("renameAllTmp", "Не удалось переименовать файл: " + file.getName());
                    allRenamedSuccessfully = false; // Если хоть один файл не удалось переименовать
                }
            }
        }

        return allRenamedSuccessfully; // Возвращаем true только если все файлы успешно переименованы
    }

//       //   File baseDir = context.getExternalFilesDir(null); // Базовая директория приложения
//         File logFolder= new File(mainFolder, "HTMLFinalContent");
//         File htmlFileTest= new File(logFolder,"HtmlLogs.txt");
//         if (!logFolder.exists()){
//             FileManagerDesktop.createCustomFolder(mainFolder,logFolder.getName());
//         }
//         if (logFolder.exists()){
//             if (!htmlFileTest.exists()){
//                 FileManagerDesktop.createFileInCustomFolder(logFolder,htmlFileTest.getName(),htmlFileContent+"  "+dateTimeManager.getFormattedTime());
//             }else {
//                 FileManagerDesktop.writeToFile(htmlFileTest,"\n\n\n\n\n\n\n\n\nNew ROW New ROW New ROW New ROW New ROW New ROW\n"+ htmlFileContent+"  "+dateTimeManager.getFormattedTime());
//             }
//         }
//
//          System.out.println(htmlFileContent);

}//endOfClass
