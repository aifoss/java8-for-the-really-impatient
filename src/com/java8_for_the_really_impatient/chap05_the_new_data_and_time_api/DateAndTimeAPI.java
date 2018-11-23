package com.java8_for_the_really_impatient.chap05_the_new_data_and_time_api;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

/**
 * Created by sofia on 12/26/16.
 */
public class DateAndTimeAPI {

    private static void runAlgorithm() {}

    public static void main(String... args) {
        Instant start = Instant.now();
        runAlgorithm();
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        long millis = timeElapsed.toMillis();
        System.out.println(millis);

        Instant start2 = Instant.now();
        for (int i = 0; i < 10; i++) {
            runAlgorithm();
        }
        Instant end2 = Instant.now();
        Duration timeElapsed2 = Duration.between(start2, end2);

        boolean overTenTimesFaster = timeElapsed.multipliedBy(10).minus(timeElapsed2).isNegative();
        System.out.println(overTenTimesFaster);
        overTenTimesFaster = timeElapsed.toNanos() * 10 < timeElapsed2.toNanos();
        System.out.println(overTenTimesFaster);

        LocalDate today = LocalDate.now();
        LocalDate alonzosBirthday = LocalDate.of(1903, 6, 14);
        System.out.println(alonzosBirthday);
        alonzosBirthday = LocalDate.of(1903, Month.JUNE, 14);
        System.out.println(alonzosBirthday);

        LocalDate programmersDay = LocalDate.of(2017, 1, 1).plusDays(255);
        System.out.println(programmersDay);

        LocalDate independenceDay = LocalDate.of(2017, 7, 4);
        LocalDate christmas = LocalDate.of(2017, Month.DECEMBER, 25);
        long daysUntilChristmas = independenceDay.until(christmas, ChronoUnit.DAYS);
        System.out.println(daysUntilChristmas);

        LocalDate endOfFeb = LocalDate.of(2017, 1, 31).plusMonths(1);
        System.out.println(endOfFeb);
        endOfFeb = LocalDate.of(2017, 3, 31).minusDays(31);
        System.out.println(endOfFeb);

        int dayValue = LocalDate.of(1900, 1, 1).getDayOfWeek().getValue();
        System.out.println(dayValue);

        DayOfWeek tuesday = DayOfWeek.SATURDAY.plus(3);
        System.out.println(tuesday);

        MonthDay december25 = MonthDay.of(12, 25);
        System.out.println(december25);

        LocalDate firstTuesday = LocalDate.of(2017, 1, 1)
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY));
        System.out.println(firstTuesday);

        TemporalAdjuster nextWorkday = w -> {
            LocalDate result = (LocalDate) w;
            do {
                result = result.plusDays(1);
            } while (result.getDayOfWeek().getValue() >= 6);
            return result;
        };

        LocalDate backToWork = today.with(nextWorkday);
        System.out.println(backToWork);
        backToWork = LocalDate.of(2016, Month.DECEMBER, 31).with(nextWorkday);
        System.out.println(backToWork);

        nextWorkday = TemporalAdjusters.ofDateAdjuster(w -> {
            LocalDate result = w;
            do {
                result = result.plusDays(1);
            } while (result.getDayOfWeek().getValue() >= 6);
            return result;
        });

        backToWork = today.minusDays(2).with(nextWorkday);
        System.out.println(backToWork);

        LocalTime rightNow = LocalTime.now();
        LocalTime bedTime = LocalTime.of(22, 30);
        LocalTime wakeUp = bedTime.plusHours(9);
        System.out.println(rightNow);
        System.out.println(bedTime);
        System.out.println(wakeUp);

        ZonedDateTime apolloLaunch = ZonedDateTime.of(1969, 7, 16, 9, 32, 0, 0, ZoneId.of("America/New_York"));
        System.out.println(apolloLaunch);

        Instant thisInstant = Instant.now();
        System.out.println(thisInstant.atZone(ZoneId.of("UTC")));

        ZonedDateTime skipped = ZonedDateTime.of(
                LocalDate.of(2013, 3, 31),
                LocalTime.of(2, 30),
                ZoneId.of("Europe/Berlin"));
        System.out.println(skipped);

        ZonedDateTime ambiguous = ZonedDateTime.of(
                LocalDate.of(2013, 10, 27),
                LocalTime.of(2, 30),
                ZoneId.of("Europe/Berlin"));
        System.out.println(ambiguous);

        ZonedDateTime anHourLater = ambiguous.plusHours(1);
        System.out.println(anHourLater);

        ZonedDateTime meeting = ZonedDateTime.of(2016, 12, 27, 11, 0, 0, 0, ZoneId.of("America/New_York"));
        ZonedDateTime nextMeeting = meeting.plus(Period.ofDays(7));
        System.out.println(nextMeeting);

        String formatted = DateTimeFormatter.ISO_DATE_TIME.format(apolloLaunch);
        System.out.println(formatted);

        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG);
        formatted = formatter.format(apolloLaunch);
        System.out.println(formatted);

        formatted = formatter.withLocale(Locale.FRENCH).format(apolloLaunch);
        System.out.println(formatted);

        formatter = DateTimeFormatter.ofPattern("E yyyy-MM-dd HH:mm");
        formatted = formatter.format(apolloLaunch);
        System.out.println(formatted);

        LocalDate churchsBirthday = LocalDate.parse("1903-06-14");
        System.out.println(churchsBirthday);

        apolloLaunch = ZonedDateTime.parse("1969-07-16 03:32:00-0400",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssxx"));
        System.out.println(apolloLaunch);
    }

}
