package com.simurg.workclock.data;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeManager {
    private final Calendar calendar;

    public DateTimeManager() {
        this.calendar = Calendar.getInstance();
    }

    // Получить текущий день
    public int getDay() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    // Получить текущий месяц (нумерация с 0, поэтому добавляем 1)
    public int getMonth() {
        return calendar.get(Calendar.MONTH) + 1; // Месяцы начинаются с 0
    }

    // Получить текущий год
    public int getYear() {
        return calendar.get(Calendar.YEAR);
    }

    // Получить текущую дату в формате "день.месяц.год"
    public String getFormattedDate() {
        calendar.setTimeInMillis(System.currentTimeMillis());  // Обновляем время для даты
        Date currentDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return dateFormat.format(currentDate);
    }

    // Получить текущее время в формате "часы:минуты:секунды"
    public String getFormattedTime() {
        calendar.setTimeInMillis(System.currentTimeMillis());  // Обновляем время для времени
        Date currentTime = calendar.getTime();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return timeFormat.format(currentTime);
    }
}
