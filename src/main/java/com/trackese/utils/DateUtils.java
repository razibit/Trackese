package com.trackese.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Utility class for date operations.
 */
public class DateUtils {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final SimpleDateFormat SDF = new SimpleDateFormat(DATE_FORMAT);

    /**
     * Get the current date as a string.
     *
     * @return Current date string
     */
    public static String getCurrentDateString() {
        return SDF.format(new Date());
    }

    /**
     * Get a list of dates for the current month.
     *
     * @return List of date strings for the current month
     */
    public static List<String> getCurrentMonthDates() {
        List<String> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        
        // Set to the first day of the current month
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        
        // Get the last day of the month
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        for (int day = 1; day <= lastDay; day++) {
            calendar.set(Calendar.DAY_OF_MONTH, day);
            dates.add(SDF.format(calendar.getTime()));
        }
        
        return dates;
    }

    /**
     * Get the last N days before a given date.
     *
     * @param dateStr Date string
     * @param n       Number of days
     * @return List of date strings
     */
    public static List<String> getLastNDays(String dateStr, int n) {
        List<String> dates = new ArrayList<>();
        
        try {
            Date date = SDF.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            
            for (int i = 0; i < n; i++) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                dates.add(SDF.format(calendar.getTime()));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return dates;
    }

    /**
     * Format a date string to a more readable format.
     *
     * @param dateStr Date string in yyyy-MM-dd format
     * @return Formatted date string
     */
    public static String formatDateForDisplay(String dateStr) {
        try {
            Date date = SDF.parse(dateStr);
            SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy");
            return displayFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr;
        }
    }
} 