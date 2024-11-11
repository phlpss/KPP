package com.example.scheduling_meetings.util;

import com.example.scheduling_meetings.domain.model.AvailabilityEntity;
import com.example.scheduling_meetings.domain.model.UserEntity;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

public class AvailabilityProcessor {
    public static Map<DayOfWeek, List<LocalTime>> findAvailableMeetingSlots(List<AvailabilityEntity> availabilities, int durationHours) {
        return processAvailability(availabilities, durationHours, false);
    }

    public static Map<DayOfWeek, Map.Entry<LocalTime, Integer>> findGreatestMeetingSlot(List<AvailabilityEntity> availabilities, int minDurationHours) {
        return processAvailability(availabilities, minDurationHours, true);
    }

    private static <T> Map<DayOfWeek, T> processAvailability(List<AvailabilityEntity> availabilities, int durationHours, boolean findGreatest) {
        if (availabilities.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<DayOfWeek, T> result = new HashMap<>();

        for (DayOfWeek day : DayOfWeek.values()) {
            List<AvailabilityEntity> dailyAvailabilities = filterDailyAvailabilities(availabilities, day);
            if (dailyAvailabilities.isEmpty()) continue;

            Map<UserEntity, List<TimeInterval>> userIntervals = mapUserIntervals(dailyAvailabilities);

            List<TimeInterval> commonIntervals = findCommonIntervals(userIntervals, durationHours);

            if (findGreatest) {
                findAndSetGreatestInterval(result, commonIntervals, day);
            } else {
                setAvailableSlots(result, commonIntervals, durationHours, day);
            }
        }
        return result;
    }

    private static List<AvailabilityEntity> filterDailyAvailabilities(List<AvailabilityEntity> availabilities, DayOfWeek day) {
        return availabilities.stream()
                .filter(a -> a.getDayOfWeek().equals(day))
                .toList();
    }

    private static Map<UserEntity, List<TimeInterval>> mapUserIntervals(List<AvailabilityEntity> dailyAvailabilities) {
        return dailyAvailabilities.stream()
                .collect(Collectors.groupingBy(
                        AvailabilityEntity::getUser,
                        Collectors.mapping(
                                a -> new TimeInterval(
                                        toUTC(a.getStartTime(), a.getUser().getTimeZone()),
                                        toUTC(a.getEndTime(), a.getUser().getTimeZone())),
                                Collectors.toList())));
    }

    private static <T> void findAndSetGreatestInterval(Map<DayOfWeek, T> result, List<TimeInterval> commonIntervals, DayOfWeek day) {
        LocalTime greatestSlotStart = null;
        int maxDuration = 0;

        for (TimeInterval interval : commonIntervals) {
            int currentDuration = (int) Duration.between(interval.start(), interval.end()).toHours();
            if (currentDuration > maxDuration) {
                maxDuration = currentDuration;
                greatestSlotStart = interval.start();
            }
        }

        if (greatestSlotStart != null) {
            result.put(day, (T) Map.entry(greatestSlotStart, maxDuration));
        }
    }

    private static <T> void setAvailableSlots(Map<DayOfWeek, T> result, List<TimeInterval> commonIntervals, int durationHours, DayOfWeek day) {
        List<TimeInterval> dividedIntervals = divideIntervalsByDuration(commonIntervals, durationHours);
        List<LocalTime> availableSlots = dividedIntervals.stream()
                .map(TimeInterval::start)
                .distinct()
                .collect(Collectors.toList());

        result.put(day, (T) availableSlots);
    }

    private static List<TimeInterval> findCommonIntervals(Map<UserEntity, List<TimeInterval>> userIntervals, int durationHours) {
        List<TimeInterval> commonIntervals = new ArrayList<>(userIntervals.values().iterator().next());

        for (List<TimeInterval> intervals : userIntervals.values()) {
            commonIntervals = intersectIntervals(commonIntervals, intervals, durationHours);
        }
        return commonIntervals;
    }

    private static List<TimeInterval> intersectIntervals(List<TimeInterval> intervals1, List<TimeInterval> intervals2, int durationHours) {
        List<TimeInterval> result = new ArrayList<>();

        for (TimeInterval i1 : intervals1) {
            for (TimeInterval i2 : intervals2) {
                LocalTime start = i1.start().isAfter(i2.start()) ? i1.start() : i2.start();
                LocalTime end = i1.end().isBefore(i2.end()) ? i1.end() : i2.end();

                if (Duration.between(start, end).toHours() >= durationHours) {
                    result.add(new TimeInterval(start, end));
                }
            }
        }
        return result;
    }

    private static List<TimeInterval> divideIntervalsByDuration(List<TimeInterval> intervals, int durationHours) {
        List<TimeInterval> dividedIntervals = new ArrayList<>();

        for (TimeInterval interval : intervals) {
            LocalTime currentStart = interval.start();
            LocalTime end = interval.end();

            while (currentStart.plusHours(durationHours).isBefore(end) || currentStart.plusHours(durationHours).equals(end)) {
                LocalTime currentEnd = currentStart.plusHours(durationHours);
                dividedIntervals.add(new TimeInterval(currentStart, currentEnd));

                var newStart = currentStart.plusHours(1);
                // handle new day
                if(newStart.isBefore(currentStart)) break;
                currentStart = newStart;
            }
        }
        return dividedIntervals;
    }

    private static LocalTime toUTC(LocalTime time, ZoneId userTimeZone) {
        return time.atDate(LocalDate.now()).atZone(userTimeZone)
                .withZoneSameInstant(ZoneOffset.UTC).toLocalTime();
    }

    private record TimeInterval(LocalTime start, LocalTime end) {
    }
}