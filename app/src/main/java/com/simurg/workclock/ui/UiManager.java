package com.simurg.workclock.ui;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import com.simurg.workclock.R;
import com.simurg.workclock.data.DateTimeManager;

public class UiManager {

    public static void toggleMainTimeVisibility(Activity activity, boolean visible) {
        TextView timeMain = activity.findViewById(R.id.timeMain);
        int visibility = visible ? View.VISIBLE : View.GONE;
        timeMain.setVisibility(visibility);
    }

    public static boolean isMainTimeVisible(Activity activity) {
        TextView timeMain = activity.findViewById(R.id.timeMain);
        return timeMain.getVisibility() == View.VISIBLE;
    }

    public static void toggleMainDateVisibility(Activity activity, boolean visible) {
        TextView dateMain = activity.findViewById(R.id.dateMain);
        int visibility = visible ? View.VISIBLE : View.GONE;
        dateMain.setVisibility(visibility);
    }

    public static boolean isMainDateVisible(Activity activity) {
        TextView dateMain = activity.findViewById(R.id.dateMain);
        return dateMain.getVisibility() == View.VISIBLE;
    }

    public static void toggleInstructorVisibility(Activity activity, boolean visible) {
        TextView instructor = activity.findViewById(R.id.instructor);
        int visibility = visible ? View.VISIBLE : View.GONE;
        instructor.setVisibility(visibility);
    }

    public static boolean isInstructorVisible(Activity activity) {
        TextView instructor = activity.findViewById(R.id.instructor);
        return instructor.getVisibility() == View.VISIBLE;
    }

    public static void toggleRecordedTimeVisibility(Activity activity, boolean visible) {
        TextView recordedTime = activity.findViewById(R.id.recordedTime);
        int visibility = visible ? View.VISIBLE : View.GONE;
        recordedTime.setVisibility(visibility);
    }

    public static boolean isRecordedTimeVisible(Activity activity) {
        TextView recordedTime = activity.findViewById(R.id.recordedTime);
        return recordedTime.getVisibility() == View.VISIBLE;
    }

    public static void toggleRecordedDateVisibility(Activity activity, boolean visible) {
        TextView recordedDate = activity.findViewById(R.id.recordedDate);
        int visibility = visible ? View.VISIBLE : View.GONE;
        recordedDate.setVisibility(visibility);
    }

    public static boolean isRecordedDateVisible(Activity activity) {
        TextView recordedDate = activity.findViewById(R.id.recordedDate);
        return recordedDate.getVisibility() == View.VISIBLE;
    }

    public static void toggleTextRegVisibility(Activity activity, boolean visible) {
        TextView textReg = activity.findViewById(R.id.textReg);
        int visibility = visible ? View.VISIBLE : View.GONE;
        textReg.setVisibility(visibility);
    }

    public static boolean isTextRegVisible(Activity activity) {
        TextView textReg = activity.findViewById(R.id.textReg);
        return textReg.getVisibility() == View.VISIBLE;
    }

    public static void toggleTextUnregVisibility(Activity activity, boolean visible) {
        TextView textUnreg = activity.findViewById(R.id.textUnreg);
        int visibility = visible ? View.VISIBLE : View.GONE;
        textUnreg.setVisibility(visibility);
    }

    public static boolean isTextUnregVisible(Activity activity) {
        TextView textUnreg = activity.findViewById(R.id.textUnreg);
        return textUnreg.getVisibility() == View.VISIBLE;
    }

    public static void toggleCodeVisibility(Activity activity, boolean visible) {
        TextView code = activity.findViewById(R.id.code);
        int visibility = visible ? View.VISIBLE : View.GONE;
        code.setVisibility(visibility);
    }

    public static boolean isCodeVisible(Activity activity) {
        TextView code = activity.findViewById(R.id.code);
        return code.getVisibility() == View.VISIBLE;
    }

    public static void setRecordedTime(Activity activity, DateTimeManager dateTimeManager) {
        TextView recordedTime = activity.findViewById(R.id.recordedTime);
        recordedTime.setText(dateTimeManager.getFormattedTime());
    }

    public static void setRecordedDate(Activity activity, DateTimeManager dateTimeManager) {
        TextView recordedDate = activity.findViewById(R.id.recordedDate);
        recordedDate.setText(dateTimeManager.getFormattedDate());
    }
    public static void setCodeText(Activity activity, String text){
        TextView code = activity.findViewById(R.id.code);
        code.setText(text);
    }
    public static void showElementForShortTime(Activity activity, int elementId) {
        // Находим элемент по ID
        View element = activity.findViewById(elementId);

        // Делаем элемент видимым
        element.setVisibility(View.VISIBLE);

        // Используем Handler, чтобы скрыть элемент через 1 секунду
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            element.setVisibility(View.GONE);
        }, 1000); // 1000 миллисекунд = 1 секунда
    }
    public static void hideElementForShortTime(Activity activity, int elementId) {
        // Находим элемент по ID
        View element = activity.findViewById(elementId);

        // Делаем элемент невидимым
        element.setVisibility(View.GONE);

        // Используем Handler, чтобы сделать элемент видимым через 1 секунду
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            element.setVisibility(View.VISIBLE);
        }, 1000); // 1000 миллисекунд = 1 секунда
    }

}
