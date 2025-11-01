package com.example.diary;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
public class DateUtils {
    public static String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(Calendar.getInstance().getTime());
    }
    public static String getTodayWithDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault());
        return sdf.format(Calendar.getInstance().getTime());
    }
}
