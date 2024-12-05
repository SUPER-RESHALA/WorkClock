package com.simurg.workclock.template;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
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

        // Найдем место для вставки новой строки: перед последним закрывающим тегом </tr>
        String placeholder = "</tr>\n</table>";

        // Вставляем новую строку перед тегом </table>
        return htmlContent.replace(placeholder, newRow + placeholder);
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
}
