package com.arabbank.hdf.uam.brain.utils;

import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;

public class DateUtils {

    public static int getBusinessDaysBetween(@NotNull LocalDateTime startDate,
                                             @NotNull LocalDateTime endDate,
                                             DayOfWeek weekendDay1,
                                             DayOfWeek weekendDay2) {
        // Check if start date is after end date and swap if necessary
        if (startDate.isAfter(endDate)) {
            LocalDateTime temp = startDate;
            startDate = endDate;
            endDate = temp;
        }

        int businessDays = 0;
        LocalDateTime date = startDate;

        while (date.isBefore(endDate) || date.isEqual(endDate)) {
            DayOfWeek day = date.getDayOfWeek();

            // Increment the count if it's not a weekend day
            if (day != weekendDay1 && day != weekendDay2) {
                businessDays++;
            }

            // Move to the next day
            date = date.plusDays(1);
        }

        return businessDays;
    }

    public static long daysSince(@NotNull LocalDateTime dateTime) {
        return Duration.between(dateTime, LocalDateTime.now()).toDays();
    }

    public static long daysUntil(@NotNull LocalDateTime dateTime){
        return Duration.between(LocalDateTime.now(), dateTime).toDays();
    }

}
