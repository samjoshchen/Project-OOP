package com.martminds.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateTimeUtil {
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_ONLY_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");


    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(DEFAULT_FORMATTER);
    }

    public static String getCurrentDate() {
        return LocalDateTime.now().format(DATE_ONLY_FORMATTER);
    }

    public static String getCurrentTime() {
        return LocalDateTime.now().format(TIME_ONLY_FORMATTER);
    }


    public static long daysBetween(String start, String end) {
        return ChronoUnit.DAYS.between(parseDateTime(start), parseDateTime(end));
    }

    public static long hoursBetween(String start, String end) {
        return ChronoUnit.HOURS.between(parseDateTime(start), parseDateTime(end));
    }

    public static long minutesBetween(String start, String end) {
        return ChronoUnit.MINUTES.between(parseDateTime(start), parseDateTime(end));
    }

    
    public static boolean isPast(String dateTime) {
        return parseDateTime(dateTime).isBefore(LocalDateTime.now());
    }

    public static boolean isFuture(String dateTime) {
        return parseDateTime(dateTime).isAfter(LocalDateTime.now());
    }


    public static LocalDateTime parseDateTime(String timestamp) {
        return LocalDateTime.parse(timestamp, DEFAULT_FORMATTER);
    }
}
