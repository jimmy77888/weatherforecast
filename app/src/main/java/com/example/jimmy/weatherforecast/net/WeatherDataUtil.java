package com.example.jimmy.weatherforecast.net;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class WeatherDataUtil {

    private static final long DAY_IN_MILLIS = TimeUnit.DAYS.toMillis(1);

    public static long getUtcLocalDate(long date) {
        long currentTime = System.currentTimeMillis();
        TimeZone localTimezone = TimeZone.getDefault();
        long timeOffset = localTimezone.getOffset(currentTime);
        long localTime = currentTime + timeOffset;

        return localTime;
    }

    public static String getFriendlyDate(Context context, long normalizedDate) {
        int flags = DateUtils.FORMAT_SHOW_WEEKDAY;
        String friendlyDate = DateUtils.formatDateTime(context, normalizedDate * 1000, flags);
        return friendlyDate;
    }
    public static long normalizeDate(long localTime) {
        long days = TimeUnit.MILLISECONDS.toDays(localTime);
        return days * DAY_IN_MILLIS;
    }
    public static boolean isDateNormalized(long date) {
        if (date % DAY_IN_MILLIS == 0) {
            return true;
        }
        return false;
    }
}
