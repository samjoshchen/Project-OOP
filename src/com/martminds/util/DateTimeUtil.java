package com.martminds.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateTimeUtil {
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_ONLY_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    // Parse current time to formatted string
    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(DEFAULT_FORMATTER);
    }

    public static String getCurrentDate() {
        return LocalDateTime.now().format(DATE_ONLY_FORMATTER);
    }

    public static String getCurrentTime() {
        return LocalDateTime.now().format(TIME_ONLY_FORMATTER);
    }

    // Calculate difference between two dates in days.
    public static long daysBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    // Calculate difference between two dates in hours.
    public static long hoursBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.HOURS.between(start, end);
    }

    // Check if a date is in the past
    public static boolean isPast(LocalDateTime dateTime) {
        return dateTime.isBefore(LocalDateTime.now());
    }

    // Check if a date is in the future
    public static boolean isFuture(LocalDateTime dateTime) {
        return dateTime.isAfter(LocalDateTime.now());
    }
}
