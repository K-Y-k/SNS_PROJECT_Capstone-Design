package com.example.sns_project.util;

import java.text.SimpleDateFormat;

public class DateUtils {

    public static String getConvertTime(long timeMils) {
        SimpleDateFormat date = new SimpleDateFormat("yyyy.MM.dd EE HH:mm");
        return date.format(timeMils);
    }
}
