package com.example.scheduling_meetings;

import com.example.scheduling_meetings.domain.model.AvailabilityEntity;
import com.example.scheduling_meetings.domain.model.UserEntity;
import com.example.scheduling_meetings.util.AvailabilityProcessor;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.time.DayOfWeek.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AvailabilityProcessorTest {

    @Test
    void testEmptyAvailabilityList() {
        List<AvailabilityEntity> availabilities = new ArrayList<>();
        int durationHours = 1;

        Map<DayOfWeek, List<LocalTime>> result = AvailabilityProcessor.findAvailableMeetingSlots(availabilities, durationHours);

        assertTrue(result.isEmpty(), "Expected empty list for no availabilities");
    }

    @Test
    void testNoOverlappingAvailabilities() {
        UserEntity user1 = UserEntity.builder().id(1L).name("User1").timeZone(ZoneId.of("UTC")).build();
        UserEntity user2 = UserEntity.builder().id(2L).name("User2").timeZone(ZoneId.of("UTC")).build();

        AvailabilityEntity availability1 = new AvailabilityEntity(1L, user1, MONDAY, LocalTime.of(9, 0), LocalTime.of(11, 0));
        AvailabilityEntity availability2 = new AvailabilityEntity(2L, user2, MONDAY, LocalTime.of(13, 0), LocalTime.of(15, 0));

        List<AvailabilityEntity> availabilities = Arrays.asList(availability1, availability2);
        int durationHours = 1;

        Map<DayOfWeek, List<LocalTime>> result = AvailabilityProcessor.findAvailableMeetingSlots(availabilities, durationHours);

        assertTrue(result.get(MONDAY).isEmpty(), "Expected no common slots between non-overlapping availabilities");
    }

    @Test
    void testOverlappingAvailabilitiesWithSufficientDuration() {
        UserEntity user1 = UserEntity.builder().id(1L).name("User1").timeZone(ZoneId.of("UTC")).build();
        UserEntity user2 = UserEntity.builder().id(2L).name("User2").timeZone(ZoneId.of("UTC")).build();

        AvailabilityEntity availability1 = new AvailabilityEntity(1L, user1, MONDAY, LocalTime.of(9, 0), LocalTime.of(11, 0));
        AvailabilityEntity availability2 = new AvailabilityEntity(2L, user2, MONDAY, LocalTime.of(10, 0), LocalTime.of(12, 0));

        List<AvailabilityEntity> availabilities = Arrays.asList(availability1, availability2);
        int durationHours = 1;

        Map<DayOfWeek, List<LocalTime>> result = AvailabilityProcessor.findAvailableMeetingSlots(availabilities, durationHours);

        assertEquals(1, result.size(), "Expected one available slot");
        assertEquals(LocalTime.of(10, 0), result.get(MONDAY).getFirst(), "Expected common slot at 10:00");
    }

    @Test
    void testOverlappingAvailabilitiesWithSufficientDurationForGroup() {
        UserEntity user1 = UserEntity.builder().id(1L).name("User1").timeZone(ZoneId.of("UTC")).build();
        UserEntity user2 = UserEntity.builder().id(2L).name("User2").timeZone(ZoneId.of("UTC")).build();
        UserEntity user3 = UserEntity.builder().id(3L).name("User3").timeZone(ZoneId.of("UTC")).build();
        UserEntity user4 = UserEntity.builder().id(4L).name("User4").timeZone(ZoneId.of("UTC")).build();

        AvailabilityEntity availability1 = new AvailabilityEntity(1L, user1, MONDAY, LocalTime.of(9, 0), LocalTime.of(15, 0));
        AvailabilityEntity availability2 = new AvailabilityEntity(2L, user2, MONDAY, LocalTime.of(10, 0), LocalTime.of(15, 0));
        AvailabilityEntity availability3 = new AvailabilityEntity(3L, user3, MONDAY, LocalTime.of(11, 0), LocalTime.of(14, 0));
        AvailabilityEntity availability4 = new AvailabilityEntity(4L, user4, MONDAY, LocalTime.of(12, 0), LocalTime.of(17, 0));

        List<AvailabilityEntity> availabilities = Arrays.asList(availability1, availability2, availability3, availability4);
        int durationHours = 1;

        Map<DayOfWeek, List<LocalTime>> result = AvailabilityProcessor.findAvailableMeetingSlots(availabilities, durationHours);

        assertEquals(2, result.get(MONDAY).size(), "Expected 2 available slots");
        assertEquals(LocalTime.of(12, 0), result.get(MONDAY).get(0), "Expected common slot at 12:00");
        assertEquals(LocalTime.of(13, 0), result.get(MONDAY).get(1), "Expected common slot at 13:00");
    }

    @Test
    void testMultipleUsersDifferentTimeZones() {
        UserEntity user1 = UserEntity.builder().id(1L).name("User1").timeZone(ZoneId.of("America/New_York")).build();
        UserEntity user2 = UserEntity.builder().id(2L).name("User2").timeZone(ZoneId.of("UTC")).build();

        // User1 is available from 9 AM to 5 PM in New York (14:00 to 22:00 UTC)
        AvailabilityEntity availability1 = new AvailabilityEntity(1L, user1, MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));
        AvailabilityEntity availability2 = new AvailabilityEntity(2L, user2, MONDAY, LocalTime.of(10, 0), LocalTime.of(18, 0));

        List<AvailabilityEntity> availabilities = Arrays.asList(availability1, availability2);
        int durationHours = 2;

        Map<DayOfWeek, List<LocalTime>> result = AvailabilityProcessor.findAvailableMeetingSlots(availabilities, durationHours);

        assertEquals(3, result.get(MONDAY).size(), "Expected 3 available slots across time zones");
        assertEquals(LocalTime.of(14, 0), result.get(MONDAY).get(0), "Expected slot to match converted availability in UTC");
        assertEquals(LocalTime.of(15, 0), result.get(MONDAY).get(1), "Expected slot to match converted availability in UTC");
        assertEquals(LocalTime.of(16, 0), result.get(MONDAY).get(2), "Expected slot to match converted availability in UTC");
    }

    @Test
    void testDifferentTimeZonesWithFullOverlap() {
        UserEntity user1 = UserEntity.builder().id(1L).name("User1").timeZone(ZoneId.of("America/New_York")).build(); // UTC-5
        UserEntity user2 = UserEntity.builder().id(2L).name("User2").timeZone(ZoneId.of("UTC")).build();

        // User1 is available from 9 AM to 5 PM in New York (14:00 to 22:00 UTC)
        AvailabilityEntity availability1 = new AvailabilityEntity(1L, user1, WEDNESDAY,
                LocalTime.of(9, 0), LocalTime.of(17, 0));
        AvailabilityEntity availability2 = new AvailabilityEntity(2L, user2, WEDNESDAY,
                LocalTime.of(14, 0), LocalTime.of(22, 0));

        List<AvailabilityEntity> availabilities = Arrays.asList(availability1, availability2);
        int durationHours = 4;

        Map<DayOfWeek, List<LocalTime>> result = AvailabilityProcessor.findAvailableMeetingSlots(availabilities, durationHours);

        assertEquals(5, result.get(WEDNESDAY).size(), "Expected one common slot across time zones with full overlap");
        assertEquals(LocalTime.of(14, 0), result.get(WEDNESDAY).get(0), "Expected slot at 14:00 UTC with 4-hour duration");
        assertEquals(LocalTime.of(15, 0), result.get(WEDNESDAY).get(1), "Expected slot at 15:00 UTC with 4-hour duration");
        assertEquals(LocalTime.of(16, 0), result.get(WEDNESDAY).get(2), "Expected slot at 16:00 UTC with 4-hour duration");
        assertEquals(LocalTime.of(17, 0), result.get(WEDNESDAY).get(3), "Expected slot at 17:00 UTC with 4-hour duration");
        assertEquals(LocalTime.of(18, 0), result.get(WEDNESDAY).get(4), "Expected slot at 18:00 UTC with 4-hour duration");
    }

    @Test
    void testNoValidMeetingSlotDueToInsufficientDuration() {
        UserEntity user1 = UserEntity.builder().id(1L).name("User1").timeZone(ZoneId.of("UTC")).build();
        UserEntity user2 = UserEntity.builder().id(2L).name("User2").timeZone(ZoneId.of("UTC")).build();

        AvailabilityEntity availability1 = new AvailabilityEntity(1L, user1, MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0));
        AvailabilityEntity availability2 = new AvailabilityEntity(2L, user2, MONDAY, LocalTime.of(9, 30), LocalTime.of(10, 30));

        List<AvailabilityEntity> availabilities = Arrays.asList(availability1, availability2);
        int durationHours = 2;

        Map<DayOfWeek, List<LocalTime>> result = AvailabilityProcessor.findAvailableMeetingSlots(availabilities, durationHours);

        assertTrue(result.get(MONDAY).isEmpty(), "Expected no slot due to insufficient overlap duration");
    }

    @Test
    void testBoundaryTimeMidnight() {
        UserEntity user1 = UserEntity.builder().id(1L).name("User1").timeZone(ZoneId.of("UTC")).build();
        UserEntity user2 = UserEntity.builder().id(2L).name("User2").timeZone(ZoneId.of("UTC")).build();

        AvailabilityEntity availability1 = new AvailabilityEntity(1L, user1, MONDAY, LocalTime.MIDNIGHT, LocalTime.of(1, 0));
        AvailabilityEntity availability2 = new AvailabilityEntity(2L, user2, MONDAY, LocalTime.MIDNIGHT, LocalTime.of(1, 0));

        List<AvailabilityEntity> availabilities = Arrays.asList(availability1, availability2);
        int durationHours = 1;

        Map<DayOfWeek, List<LocalTime>> result = AvailabilityProcessor.findAvailableMeetingSlots(availabilities, durationHours);

        assertEquals(1, result.size(), "Expected one slot at midnight");
        assertEquals(LocalTime.MIDNIGHT, result.get(MONDAY).getFirst(), "Expected midnight slot as the boundary time");
    }

    @Test
    void testMultipleUsersAcrossTimeZonesWithVariousAvailabilities() {
        UserEntity user1 = UserEntity.builder().id(1L).name("User1").timeZone(ZoneId.of("UTC")).build();
        UserEntity user2 = UserEntity.builder().id(2L).name("User2").timeZone(ZoneId.of("Asia/Yerevan")).build();
        UserEntity user3 = UserEntity.builder().id(3L).name("User3").timeZone(ZoneId.of("Asia/Tokyo")).build();
        UserEntity user4 = UserEntity.builder().id(4L).name("User4").timeZone(ZoneId.of("America/Chicago")).build();
        UserEntity user5 = UserEntity.builder().id(5L).name("User5").timeZone(ZoneId.of("America/Chicago")).build();

        AvailabilityEntity availability1 = new AvailabilityEntity(1L, user1, MONDAY, LocalTime.of(9, 0), LocalTime.of(11, 0));
        AvailabilityEntity availability2 = new AvailabilityEntity(2L, user1, MONDAY, LocalTime.of(13, 0), LocalTime.of(15, 0));

        AvailabilityEntity availability3 = new AvailabilityEntity(3L, user2, MONDAY, LocalTime.of(14, 0), LocalTime.of(16, 0)); // Yerevan: 10:00-12:00 UTC
        AvailabilityEntity availability4 = new AvailabilityEntity(4L, user2, MONDAY, LocalTime.of(18, 0), LocalTime.of(20, 0)); // Yerevan: 14:00-16:00 UTC

        AvailabilityEntity availability5 = new AvailabilityEntity(5L, user3, MONDAY, LocalTime.of(18, 0), LocalTime.of(20, 0)); // Tokyo: 9:00-11:00 UTC
        AvailabilityEntity availability6 = new AvailabilityEntity(6L, user3, MONDAY, LocalTime.of(21, 0), LocalTime.of(23, 0)); // Tokyo: 12:00-14:00 UTC

        AvailabilityEntity availability7 = new AvailabilityEntity(7L, user4, MONDAY, LocalTime.of(4, 0), LocalTime.of(6, 0)); // Chicago: 10:00-12:00 UTC
        AvailabilityEntity availability8 = new AvailabilityEntity(8L, user4, MONDAY, LocalTime.of(14, 0), LocalTime.of(16, 0)); // Chicago: 20:00-22:00 UTC

        AvailabilityEntity availability9 = new AvailabilityEntity(9L, user5, MONDAY, LocalTime.of(10, 0), LocalTime.of(12, 0)); // Chicago: 16:00-18:00 UTC

        List<AvailabilityEntity> availabilities = Arrays.asList(availability1, availability2, availability3,
                availability4, availability5, availability6, availability7, availability8);
        int durationHours = 1;

        Map<DayOfWeek, List<LocalTime>> result = AvailabilityProcessor.findAvailableMeetingSlots(availabilities, durationHours);

        // Check expected results (ensure they align with your expectations)
        assertEquals(1, result.size(), "Expected 1 available slot on Monday.");
        assertEquals(LocalTime.of(10, 0), result.get(MONDAY).getFirst(), "Expected common slot at 10:00 UTC.");
    }

    @Test
    void testGroupMeetingSlotsWithWideRangeOfTimesAcrossDifferentDays() {
        UserEntity user1 = UserEntity.builder().id(1L).name("User1").timeZone(ZoneId.of("UTC")).build();
        UserEntity user2 = UserEntity.builder().id(2L).name("User2").timeZone(ZoneId.of("Asia/Yerevan")).build();
        UserEntity user3 = UserEntity.builder().id(3L).name("User3").timeZone(ZoneId.of("Asia/Tokyo")).build();
        UserEntity user4 = UserEntity.builder().id(4L).name("User4").timeZone(ZoneId.of("America/Chicago")).build();

        AvailabilityEntity availability1 = new AvailabilityEntity(1L, user1, TUESDAY, LocalTime.of(8, 0), LocalTime.of(12, 0));
        AvailabilityEntity availability2 = new AvailabilityEntity(2L, user1, WEDNESDAY, LocalTime.of(14, 0), LocalTime.of(18, 0));

        AvailabilityEntity availability3 = new AvailabilityEntity(3L, user2, TUESDAY, LocalTime.of(11, 0), LocalTime.of(15, 0)); // Yerevan: 7:00-11:00 UTC
        AvailabilityEntity availability4 = new AvailabilityEntity(4L, user2, WEDNESDAY, LocalTime.of(16, 0), LocalTime.of(18, 0)); // Yerevan: 12:00-14:00 UTC

        AvailabilityEntity availability5 = new AvailabilityEntity(5L, user3, TUESDAY, LocalTime.of(14, 0), LocalTime.of(18, 0)); // Tokyo: 5:00-9:00 UTC
        AvailabilityEntity availability6 = new AvailabilityEntity(6L, user3, WEDNESDAY, LocalTime.of(16, 0), LocalTime.of(20, 0)); // Tokyo: 7:00-11:00 UTC

        AvailabilityEntity availability7 = new AvailabilityEntity(7L, user4, TUESDAY, LocalTime.of(0, 0), LocalTime.of(6, 0)); // Chicago: 14:00-16:00 UTC -> 6-12
        AvailabilityEntity availability8 = new AvailabilityEntity(8L, user4, WEDNESDAY, LocalTime.of(15, 0), LocalTime.of(17, 0)); // Chicago: 21:00-23:00 UTC

        List<AvailabilityEntity> availabilities = Arrays.asList(availability1, availability2, availability3, availability4, availability5, availability6, availability7, availability8);
        int durationHours = 1;

        Map<DayOfWeek, List<LocalTime>> result = AvailabilityProcessor.findAvailableMeetingSlots(availabilities, durationHours);

        assertEquals(1, result.get(TUESDAY).size(), "Expected 1 available slot on Tuesday.");
        assertEquals(LocalTime.of(8, 0), result.get(TUESDAY).getFirst(), "Expected common slot at 8:00 UTC.");
        assertEquals(0, result.get(WEDNESDAY).size(), "Expected 0 available slot on Wednesday.");
    }

    @Test
    void testSingleUser_GreatestMeetingSlot() {
        UserEntity user = UserEntity.builder().id(1L).name("User").timeZone(ZoneId.of("UTC")).build();

        AvailabilityEntity availability = new AvailabilityEntity(1L, user, DayOfWeek.MONDAY,
                LocalTime.of(9, 0), LocalTime.of(17, 0));

        List<AvailabilityEntity> availabilities = List.of(availability);

        Map<DayOfWeek, Map.Entry<LocalTime, Integer>> result = AvailabilityProcessor.findGreatestMeetingSlot(availabilities, 1);

        assertEquals(LocalTime.of(9, 0), result.get(DayOfWeek.MONDAY).getKey(), "Expected meeting slot starts at 9:00");
        assertEquals(8, result.get(DayOfWeek.MONDAY).getValue(), "Expected 8 hours meeting slot duration");
    }

    @Test
    void testWithDifferentTimeZone_GreatestMeetingSlot() {
        UserEntity user1 = UserEntity.builder().id(1L).name("User1").timeZone(ZoneId.of("America/New_York")).build(); // UTC-5
        UserEntity user2 = UserEntity.builder().id(2L).name("User2").timeZone(ZoneId.of("UTC")).build();

        // User1 is available from 9 AM to 5 PM in New York (14:00 to 22:00 UTC)
        AvailabilityEntity availability1 = new AvailabilityEntity(1L, user1, WEDNESDAY,
                LocalTime.of(9, 0), LocalTime.of(17, 0));
        AvailabilityEntity availability2 = new AvailabilityEntity(2L, user2, WEDNESDAY,
                LocalTime.of(14, 0), LocalTime.of(22, 0));

        List<AvailabilityEntity> availabilities = Arrays.asList(availability1, availability2);

        Map<DayOfWeek, Map.Entry<LocalTime, Integer>> result = AvailabilityProcessor.findGreatestMeetingSlot(availabilities, 1);

        assertEquals(LocalTime.of(14, 0), result.get(WEDNESDAY).getKey(), "Expected meeting slot starts at 14:00");
        assertEquals(8, result.get(WEDNESDAY).getValue(), "Expected 8 hours meeting slot duration");
    }

    @Test
    void testNoCommonMeetingSlot_GreatestMeetingSlot() {
        UserEntity user1 = UserEntity.builder().id(1L).name("User1").timeZone(ZoneId.of("UTC")).build();
        UserEntity user2 = UserEntity.builder().id(2L).name("User2").timeZone(ZoneId.of("UTC")).build();

        AvailabilityEntity availability1 = new AvailabilityEntity(1L, user1, DayOfWeek.FRIDAY,
                LocalTime.of(9, 0), LocalTime.of(11, 0));
        AvailabilityEntity availability2 = new AvailabilityEntity(2L, user2, DayOfWeek.FRIDAY,
                LocalTime.of(13, 0), LocalTime.of(15, 0));

        List<AvailabilityEntity> availabilities = List.of(availability1, availability2);

        Map<DayOfWeek, Map.Entry<LocalTime, Integer>> result = AvailabilityProcessor.findGreatestMeetingSlot(availabilities, 1);

        assertEquals(0, result.size(), "Expected no common meeting slot");
    }

    @Test
    void testEmptyAvailabilityList_GreatestMeetingSlot() {
        List<AvailabilityEntity> availabilities = List.of();

        Map<DayOfWeek, Map.Entry<LocalTime, Integer>> result = AvailabilityProcessor.findGreatestMeetingSlot(availabilities, 1);

        assertEquals(0, result.size(), "Expected empty result for no availabilities");
    }

    @Test
    void testOverlappingAvailabilitiesWithTwoHourMaxOnTuesday() {
        UserEntity user1 = UserEntity.builder().id(1L).name("User1").timeZone(ZoneId.of("Europe/London")).build();
        UserEntity user2 = UserEntity.builder().id(2L).name("User2").timeZone(ZoneId.of("Europe/London")).build();

        AvailabilityEntity availability1 = new AvailabilityEntity(1L, user1, TUESDAY, LocalTime.of(11, 0), LocalTime.of(15, 0));
        AvailabilityEntity availability2 = new AvailabilityEntity(2L, user2, TUESDAY, LocalTime.of(12, 0), LocalTime.of(14, 0));

        List<AvailabilityEntity> availabilities = Arrays.asList(availability1, availability2);
        int durationHours = 2;

        Map<DayOfWeek, List<LocalTime>> result = AvailabilityProcessor.findAvailableMeetingSlots(availabilities, durationHours);

        assertEquals(1, result.size(), "Expected one available slot on Tuesday");
        assertEquals(LocalTime.of(12, 0), result.get(TUESDAY).getFirst(), "Expected available slot at 12:00 on Tuesday");
    }

    @Test
    void testNonConsecutiveHoursNoCommonSlotOnWednesday() {
        UserEntity user1 = UserEntity.builder().id(1L).name("User1").timeZone(ZoneId.of("Europe/London")).build();
        UserEntity user2 = UserEntity.builder().id(2L).name("User2").timeZone(ZoneId.of("Europe/London")).build();

        AvailabilityEntity availability1 = new AvailabilityEntity(3L, user1, WEDNESDAY, LocalTime.of(11, 0), LocalTime.of(13, 0));
        AvailabilityEntity availability2 = new AvailabilityEntity(4L, user2, WEDNESDAY, LocalTime.of(14, 0), LocalTime.of(16, 0));

        List<AvailabilityEntity> availabilities = Arrays.asList(availability1, availability2);
        int durationHours = 2;

        Map<DayOfWeek, List<LocalTime>> result = AvailabilityProcessor.findAvailableMeetingSlots(availabilities, durationHours);

        assertEquals(0, result.get(WEDNESDAY).size(), "Expected no common available slots on Wednesday due to non-consecutive hours");
    }

    @Test
    void testDifferentTimeZonesWithOverlapInUTCOnTuesday() {
        UserEntity user1 = UserEntity.builder().id(1L).name("User1").timeZone(ZoneId.of("Europe/London")).build();  // UTC
        UserEntity user2 = UserEntity.builder().id(2L).name("User2").timeZone(ZoneId.of("Europe/Kyiv")).build();    // UTC+2

        AvailabilityEntity availability1 = new AvailabilityEntity(1L, user1, TUESDAY, LocalTime.of(18, 0), LocalTime.of(23, 0));
        AvailabilityEntity availability2 = new AvailabilityEntity(2L, user2, WEDNESDAY, LocalTime.of(0, 0), LocalTime.of(3, 0)); // 22:00 - 01:00

        List<AvailabilityEntity> availabilities = Arrays.asList(availability1, availability2);
        int durationHours = 2;

        Map<DayOfWeek, List<LocalTime>> result = AvailabilityProcessor.findAvailableMeetingSlots(availabilities, durationHours);

        assertEquals(1, result.get(TUESDAY).size(), "Expected one available slot due to time zone overlap");
        assertEquals(LocalTime.of(22, 0), result.get(TUESDAY).getFirst(), "Expected available slot at 22:00 UTC on Tuesday");
    }
}