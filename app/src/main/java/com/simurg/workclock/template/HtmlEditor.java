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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
    public String addNewRowToHtml(String finalContent, String code, String date, String time, String note) {
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
int indexOfTableTag=finalContent.indexOf("</table>");
StringBuilder stringBuilder=new StringBuilder(finalContent);
stringBuilder.insert(indexOfTableTag,newRow);
        return stringBuilder.toString();
    }
    public static String addNewRowToHtml(String finalContent,String rowsToAdd) {
        // Формируем строку, которую добавим в таблицу

        int indexOfTableTag=finalContent.indexOf("</table>");
        StringBuilder stringBuilder=new StringBuilder(finalContent);
        stringBuilder.insert(indexOfTableTag,rowsToAdd);
        return stringBuilder.toString();
    }


    public static String extractDataRowsFromFile(File htmlFile) throws IOException {
        StringBuilder dataRows = new StringBuilder();

        // Парсинг HTML-файла
        Document document = Jsoup.parse(htmlFile, "UTF-8");

        // Поиск таблицы с данными
        Element table = document.selectFirst("table#center table");
        if (table == null) {
            throw new IOException("Таблица не найдена в файле: " + htmlFile.getName());
        }

        // Получение всех строк, кроме заголовков
        Elements rows = table.select("tr:gt(1)"); // Пропускаем первые 2 строки (заголовок)

        // Объединение строк в одну строку
        for (Element row : rows) {
            dataRows.append(row.outerHtml()).append("\n");
        }

        return dataRows.toString();
    }


    public static String extractDataRowsFromString(String htmlContent) {
        // Парсим HTML строку в Document
        Document doc = Jsoup.parse(htmlContent);

        // Извлекаем все строки таблицы, пропуская заголовок
        Elements rows = doc.select("table#center table tr").not(":nth-child(1), :nth-child(2)");

        // Формируем строку с данными, которые содержат строки таблицы
        StringBuilder dataRows = new StringBuilder();
        for (Element row : rows) {
            dataRows.append(row.toString()).append("\n");  // Добавляем каждую строку с переводом строки
        }

        return dataRows.toString();  // Возвращаем все строки как одну строку
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
