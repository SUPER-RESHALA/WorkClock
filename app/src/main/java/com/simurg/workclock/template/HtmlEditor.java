package com.simurg.workclock.template;

import static com.simurg.workclock.file.FileManagerDesktop.createCustomFolder;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class HtmlEditor {

    private final Context context;
    private final String templateFileName;

    public HtmlEditor(Context context, String templateFileName) {
        this.context = context;
        this.templateFileName = templateFileName;
    }

    // Метод для загрузки шаблона из папки assets
    public String loadTemplate() {
        StringBuilder htmlContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(context.getAssets().open(templateFileName), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                htmlContent.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return htmlContent.toString();
    }

    // Метод для добавления данных в HTML
    public String addNewRowToHtml(String htmlContent, String code, String date, String time, String note) {
        // Формируем строку, которую добавим в таблицу
        String newRow = String.format(
                "<tr>\n" +
                        "    <td>%s</td>\n" +
                        "    <td>%s</td>\n" +
                        "    <td>%s</td>\n" +
                        "    <td>%s</td>\n" +
                        "</tr>\n",
                code, date, time, note
        );
int indexOfTableTag=htmlContent.indexOf("</table>");
StringBuilder stringBuilder=new StringBuilder(htmlContent);
stringBuilder.insert(indexOfTableTag,newRow);
        return stringBuilder.toString();
    }

    // Метод для сохранения измененного HTML в файл
    public File saveToFile(String htmlContent, String outputFileName) {
        File outputFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), outputFileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write(htmlContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputFile;
    }

    public void saveToCustomFolder(String htmlContent, String outputFileName, Context context, String customFolderName) {
        File customFolder = createCustomFolder(context, customFolderName);
        if (customFolder == null) {
            Log.e("FileManager", "Папка не была создана, сохранение файла невозможно.");
            return;
        }

        File outputFile = new File(customFolder, outputFileName); // Сохраняем в пользовательскую папку
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write(htmlContent);
            Log.i("FileManager", "Файл успешно сохранен: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e("FileManager", "Ошибка при сохранении файла: " + e.getMessage());
        }

    }

}//END OF CLASS
