package tools;

import java.time.LocalTime;

public abstract class TimeTools {
    public static int minusLocalTime(LocalTime diff1, LocalTime diff2) {
        LocalTime diff = diff1.minusHours(diff2.getHour())
                .minusMinutes(diff2.getMinute())
                .minusSeconds(diff2.getSecond());

        return (diff.getHour() * 60 * 60) + (diff.getMinute() * 60) + (diff.getSecond());
    }

    public static LocalTime plusLocalTime(LocalTime diff1, long diff2) {
        LocalTime diff = diff1.plusSeconds(diff2);

        return diff;
    }
    public static LocalTime minusLocalTime(LocalTime diff1, long diff2) {
        LocalTime diff = diff1.minusSeconds(diff2);

        return diff;
    }
}
