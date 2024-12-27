package com.simurg.workclock.file;

import com.simurg.workclock.entity.Employee;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class CsvReader {
   private  final AtomicBoolean isUpdating = new AtomicBoolean(false);
    public void startUpdate()throws IllegalStateException {
        if (!isUpdating.compareAndSet(false, true)) {
            throw new IllegalStateException("CSV обновляется, операция запрещена.");
        }
    }

    public boolean checkIsUpdating() {
        return isUpdating.get();
    }

    public void finishUpdate() {
        isUpdating.set(false);
    }
    public Map<String, Employee> readCsvToMap(String filePath) {
        Map<String, Employee> map = new HashMap<>();
        BufferedReader br = null;

        try {
            // Открываем файл через InputStreamReader
            FileInputStream fis = new FileInputStream(new File(filePath));
            br = new BufferedReader(new InputStreamReader(fis));

            String line;
            while ((line = br.readLine()) != null) {
                // Разделяем строку по разделителю ";"
                String[] parts = line.split(";");
                if (parts.length >= 3) {
                    String key = parts[0].trim(); // Убираем пробелы из ключа
                    String code = parts[1].trim(); // Убираем пробелы из табельного номера
                    String subdivision = parts[2].trim();// Убираем пробелы из подразделения



                    // Убираем первый символ 'N', если он есть
                    if (code.startsWith("N")) {
                        code = code.substring(1);
                    }

                    // Создаем объект Employee
                    Employee employee = new Employee();
                    employee.setSubdivision(subdivision);
                    employee.setCode(code);

                    // Добавляем в карту
                    map.put(key, employee);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Закрываем BufferedReader, если он был открыт
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return map;
    }



}
