package com.simurg.workclock.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateTimeManager {

    private final Locale locale;

    public DateTimeManager(Locale locale) {
        this.locale = locale;
    }

    // Получить текущую дату и время в формате "dd-MM-yyyy HH:mm:ss"
    public String getCurrentDateTime() {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", locale);
        return format.format(Calendar.getInstance().getTime());
    }

    // Получить только текущую дату
    public String getCurrentDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", locale);
        return format.format(Calendar.getInstance().getTime());
    }

    // Получить только текущее время
    public String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", locale);
        return format.format(Calendar.getInstance().getTime());
    }

    // Проверить, является ли текущий год високосным
    public boolean isLeapYear() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    // Получить разницу между двумя датами в днях
    public long getDaysDifference(String date1, String date2, String pattern) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat(pattern, locale);
        long diff = Math.abs(format.parse(date1).getTime() - format.parse(date2).getTime());
        return diff / (1000 * 60 * 60 * 24);
    }
}
