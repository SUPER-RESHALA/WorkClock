package com.simurg.workclock.ui;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

public class UiManager {
    public static  void toggleMainTimeVisibility(Activity activity, boolean visible, TextView timeMain){
        int visibility = visible ? View.VISIBLE : View.GONE;
        timeMain.setVisibility(visibility);
    }
    public static boolean isMainTimeVisible(Activity activity, TextView timeMain) {
        // Проверяем, видим ли TextView на экране
        int visibility = timeMain.getVisibility();
        return visibility == View.VISIBLE;
    }

    public static  void toggleMainDateVisibility(Activity activity, boolean visible, TextView dateMain){
        int visibility = visible ? View.VISIBLE : View.GONE;
        dateMain.setVisibility(visibility);
    }
    public static boolean isMainDateVisible(Activity activity, TextView dateMain) {
        // Проверяем, видим ли TextView на экране
        int visibility = dateMain.getVisibility();
        return visibility == View.VISIBLE;
    }

    public static  void toggleInstructorVisibility(Activity activity, boolean visible, TextView instructor){
        int visibility = visible ? View.VISIBLE : View.GONE;
        instructor.setVisibility(visibility);
    }
    public static boolean isInstructorVisible(Activity activity, TextView instructor) {
        // Проверяем, видим ли TextView на экране
        int visibility = instructor.getVisibility();
        return visibility == View.VISIBLE;
    }

    public static  void toggleRecordedTimeVisibility(Activity activity, boolean visible, TextView recordedTime){
        int visibility = visible ? View.VISIBLE : View.GONE;
        recordedTime.setVisibility(visibility);
    }
    public static boolean isRecordedTimeVisible(Activity activity, TextView recordedTime) {
        // Проверяем, видим ли TextView на экране
        int visibility = recordedTime.getVisibility();
        return visibility == View.VISIBLE;
    }
    public static  void toggleRecordedDateVisibility(Activity activity, boolean visible, TextView recordedDate){
        int visibility = visible ? View.VISIBLE : View.GONE;
        recordedDate.setVisibility(visibility);
    }
    public static boolean isRecordedDateVisible(Activity activity, TextView recordedDate) {
        // Проверяем, видим ли TextView на экране
        int visibility = recordedDate.getVisibility();
        return visibility == View.VISIBLE;
    }
    public static  void toggleTextRegVisibility(Activity activity, boolean visible, TextView textReg){
        int visibility = visible ? View.VISIBLE : View.GONE;
        textReg.setVisibility(visibility);
    }
    public static boolean isTextRegVisible(Activity activity, TextView textReg) {
        // Проверяем, видим ли TextView на экране
        int visibility = textReg.getVisibility();
        return visibility == View.VISIBLE;
    }
    public static  void toggleTextUnregVisibility(Activity activity, boolean visible, TextView textUnreg){
        int visibility = visible ? View.VISIBLE : View.GONE;
        textUnreg.setVisibility(visibility);
    }
    public static boolean isTextUnregVisible(Activity activity, TextView textUnreg) {
        // Проверяем, видим ли TextView на экране
        int visibility = textUnreg.getVisibility();
        return visibility == View.VISIBLE;
    }
    public static  void toggleCodeVisibility(Activity activity, boolean visible, TextView code){
        int visibility = visible ? View.VISIBLE : View.GONE;
        code.setVisibility(visibility);
    }
    public static boolean isCodeVisible(Activity activity, TextView code) {
        // Проверяем, видим ли TextView на экране
        int visibility = code.getVisibility();
        return visibility == View.VISIBLE;
    }
    public static void setRecordedTime (TextView recordedTime, String text){
       recordedTime.setText(text);
    }
    public static void setRecordedDate (TextView recordedDate, String text){
        recordedDate.setText(text);
    }
}
