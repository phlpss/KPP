//package com.example.scheduling_meetings.service;
//
//import com.example.scheduling_meetings.domain.dto.AvailabilityDto;
//import com.example.scheduling_meetings.domain.model.AvailabilityEntity;
//import com.example.scheduling_meetings.domain.model.UserEntity;
//import com.example.scheduling_meetings.mappers.Mapper;
//import com.example.scheduling_meetings.repository.AvailabilityRepository;
//import com.example.scheduling_meetings.repository.UserRepository;
//import com.example.scheduling_meetings.util.AvailabilityProcessor;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.DayOfWeek;
//import java.time.LocalTime;
//import java.util.List;
//import java.util.Map;
//
//@RequiredArgsConstructor
//@Service
//public class MeetingService {
//
//    private final UserRepository userRepository;
//    private final AvailabilityRepository availabilityRepository;
//    private final Mapper<AvailabilityEntity, AvailabilityDto> availabilityMapper;
//
//    @Transactional
//    public UserEntity createUser(UserEntity user) {
//        return userRepository.save(user);
//    }
//
//    @Transactional
//    public AvailabilityDto createAvailability(AvailabilityDto availabilityDto) {
//        var availabilityEntity = availabilityMapper.mapFrom(availabilityDto);
//        availabilityRepository.save(availabilityEntity);
//        return availabilityMapper.mapTo(availabilityEntity);
//    }
//
//    public void deleteUser(Long userId) {
//        var user = userRepository.findById(userId);
//        userRepository.delete(user.orElseThrow());
//    }
//
//    public void deleteAllUsers() {
//        userRepository.deleteAll();
//    }
//
//    public void deleteAvailability(Long availabilityId) {
//        var availability = availabilityRepository.findById(availabilityId);
//        availabilityRepository.delete(availability.orElseThrow());
//    }
//
//    public void deleteAllAvailabilities() {
//        availabilityRepository.deleteAll();
//    }
//
//    public void deleteAvailabilitiesOfUser(Long userId) {
//        var availabilitiesByUser = availabilityRepository.findByUserId(userId);
//        availabilityRepository.deleteAllInBatch(availabilitiesByUser);
//    }
//
//    public Map<DayOfWeek, List<LocalTime>> findAvailableMeetingSlots(List<AvailabilityEntity> availabilities, int durationHours) {
//        return AvailabilityProcessor.findAvailableMeetingSlots(availabilities, durationHours);
//    }
//
//    public Map<DayOfWeek, Map.Entry<LocalTime, Integer>> findGreatestMeetingSlot(List<AvailabilityEntity> availabilities) {
//        return AvailabilityProcessor.findGreatestMeetingSlot(availabilities, 1);
//    }
//
//    public List<AvailabilityEntity> getAvailabilityByUserId(Long id) {
//        return availabilityRepository.findByUserId(id);
//    }
//
//    public List<UserEntity> getAllUsers() {
//        return userRepository.findAll();
//    }
//
//    public List<AvailabilityEntity> getAllAvailabilities() {
//        return availabilityRepository.findAll();
//    }
//}

package com.example.scheduling_meetings.service;

import com.example.scheduling_meetings.domain.dto.AvailabilityDto;
import com.example.scheduling_meetings.domain.model.AvailabilityEntity;
import com.example.scheduling_meetings.domain.model.UserEntity;
import com.example.scheduling_meetings.mappers.Mapper;
import com.example.scheduling_meetings.repository.AvailabilityRepository;
import com.example.scheduling_meetings.repository.UserRepository;
import com.example.scheduling_meetings.util.AvailabilityProcessor;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MeetingService {

    private static final Clock UTC_CLOCK = Clock.systemUTC();
    private final UserRepository userRepository;
    private final AvailabilityRepository availabilityRepository;
    private final Mapper<AvailabilityEntity, AvailabilityDto> availabilityMapper;

    @Transactional
    public UserEntity createUser(UserEntity user) {
        return userRepository.save(user);
    }

    @Transactional
    public AvailabilityDto createAvailability(AvailabilityDto availabilityDto) {
        var availabilityEntity = availabilityMapper.mapFrom(availabilityDto);
        availabilityRepository.save(availabilityEntity);
        return availabilityMapper.mapTo(availabilityEntity);
    }

    public void deleteUser(Long userId) {
        var user = userRepository.findById(userId);
        userRepository.delete(user.orElseThrow());
    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    public void deleteAvailability(Long availabilityId) {
        var availability = availabilityRepository.findById(availabilityId);
        availabilityRepository.delete(availability.orElseThrow());
    }

    public void deleteAllAvailabilities() {
        availabilityRepository.deleteAll();
    }

    public void deleteAvailabilitiesOfUser(Long userId) {
        var availabilitiesByUser = availabilityRepository.findByUserId(userId);
        availabilityRepository.deleteAllInBatch(availabilitiesByUser);
    }

    public Map<DayOfWeek, List<LocalTime>> findAvailableMeetingSlots(List<AvailabilityEntity> availabilities, int durationHours) {
        return AvailabilityProcessor.findAvailableMeetingSlots(availabilities, durationHours);
    }

    public Map<DayOfWeek, List<LocalTime>> findAvailableMeetingSlots(int durationHours) {
        var availabilities = this.getAvailabilitiesInUtc();
        long totalUsers = getAllUsers().size();
        var availabilitiesGroupedByDay = availabilities.stream().collect(Collectors.groupingBy(AvailabilityEntity::getDayOfWeek));

        var container = new PossibleMeetingContainer();

        for (var entry : availabilitiesGroupedByDay.entrySet()) {
            for (AvailabilityEntity availabilityEntity : entry.getValue()) {
                List<LocalTime> hoursBetween = getHoursBetween(availabilityEntity.getStartTime(), availabilityEntity.getEndTime());
                for (LocalTime localTime : hoursBetween) {
                    container.createOrIncrementUsersCount(entry.getKey(), localTime);
                }
            }
        }

        List<PossibleMeetingHour> meetingHours = container.getMeetingHours().stream()
                .filter(el -> el.getUsersCount() == totalUsers)
                .toList();

        Map<DayOfWeek, List<LocalTime>> meeetingHoursByDay = meetingHours.stream()
                .collect(Collectors.groupingBy(
                        PossibleMeetingHour::getDayOfWeek,
                        Collectors.mapping(PossibleMeetingHour::getTime, Collectors.toList())
                ));

        return meeetingHoursByDay.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> findConsecutiveTimes(entry.getValue(), durationHours)
                ));

    }

    private List<LocalTime> findConsecutiveTimes(List<LocalTime> times, int durationHours) {
        // Sort times to ensure they are in order for consecutive checks
        List<LocalTime> sortedTimes = times.stream().sorted().toList();
        List<LocalTime> filteredTimes = new ArrayList<>();

        for (int i = 0; i <= sortedTimes.size() - durationHours; i++) {
            boolean isConsecutive = true;

            // Check if we have a consecutive block of 'durationHours'
            for (int j = 0; j < durationHours - 1; j++) {
                if (!sortedTimes.get(i + j).plusHours(1).equals(sortedTimes.get(i + j + 1))) {
                    isConsecutive = false;
                    break;
                }
            }

            // If a consecutive block is found, add only the start time
            if (isConsecutive) {
                filteredTimes.add(sortedTimes.get(i));
            }
        }

        return filteredTimes;
    }

    class PossibleMeetingContainer {
        private List<PossibleMeetingHour> meetingHours = new ArrayList<>();

        public void createOrIncrementUsersCount(DayOfWeek dayOfWeek, LocalTime meetingTime) {
            Optional<PossibleMeetingHour> meetingHour = meetingHours.stream()
                    .filter(el -> el.getDayOfWeek().equals(dayOfWeek) && el.getTime().equals(meetingTime))
                    .findFirst();

            meetingHour.ifPresentOrElse(
                    PossibleMeetingHour::incrementUsersCount,
                    () -> meetingHours.add(new PossibleMeetingHour(dayOfWeek, meetingTime))
            );
        }

        public List<PossibleMeetingHour> getMeetingHours() {
            return meetingHours;
        }
    }

    public List<LocalTime> getHoursBetween(LocalTime startTime, LocalTime endTime) {
        List<LocalTime> hours = new ArrayList<>();
        LocalTime current = startTime;

        while (current.isBefore(endTime)) {
            hours.add(current);
            current = current.plusHours(1);
            if (!(current.isBefore(LocalTime.MAX) && current.isBefore(LocalTime.of(23, 0)))) {
                break;
            }
        }

        return hours;
    }

    @Data
    static final class PossibleMeetingHour {
        private final DayOfWeek dayOfWeek;
        private final LocalTime time;
        @EqualsAndHashCode.Exclude
        private Long usersCount;

        public void incrementUsersCount() {
            this.usersCount += 1;
        }

        PossibleMeetingHour(DayOfWeek dayOfWeek, LocalTime time) {
            this.dayOfWeek = dayOfWeek;
            this.time = time;
            this.usersCount = 1L;
        }
    }

    public List<AvailabilityEntity> getAvailabilitiesInUtc() {
        var availabilities = this.getAllAvailabilities();
        return availabilities.stream()
                .flatMap(availability -> splitAndConvertToUtc(availability).stream())
                .collect(Collectors.toList());
    }

    private List<AvailabilityEntity> splitAndConvertToUtc(AvailabilityEntity availability) {
        ZoneId userZoneId = availability.getUser().getTimeZone();

        ZonedDateTime utcStart = availability.getStartTime()
                .atDate(LocalDate.now(UTC_CLOCK))
                .atZone(userZoneId)
                .withZoneSameInstant(ZoneOffset.UTC);

        ZonedDateTime utcEnd = availability.getEndTime()
                .atDate(LocalDate.now(UTC_CLOCK))
                .atZone(userZoneId)
                .withZoneSameInstant(ZoneOffset.UTC);

        if (utcStart.toLocalDate().equals(utcEnd.toLocalDate())) {
            return List.of(createAvailabilityEntity(availability, availability.getDayOfWeek(), utcStart.toLocalTime(), utcEnd.toLocalTime()));
        }

        // First entry: from start time to midnight
        ZonedDateTime endOfDay = utcStart.toLocalDate()
                .atTime(LocalTime.MAX)
                .atZone(ZoneOffset.UTC);

        var dayOfWeek1 = utcStart.toLocalTime().isBefore(availability.getStartTime()) ? availability.getDayOfWeek() : availability.getDayOfWeek().minus(1);

        AvailabilityEntity firstEntry = createAvailabilityEntity(availability, dayOfWeek1, utcStart.toLocalTime(), endOfDay.toLocalTime());

        // Second entry: from midnight to end time
        AvailabilityEntity secondEntry = createAvailabilityEntity(availability, dayOfWeek1.plus(1), LocalTime.of(0, 0), utcEnd.toLocalTime());

        return List.of(firstEntry, secondEntry);
    }


    private AvailabilityEntity createAvailabilityEntity(AvailabilityEntity availability,
                                                        DayOfWeek dayOfWeek,
                                                        LocalTime startDateTime,
                                                        LocalTime endDateTime) {
        return AvailabilityEntity.builder()
                .id(availability.getId())
                .user(availability.getUser())
                .dayOfWeek(dayOfWeek)
                .startTime(startDateTime)
                .endTime(endDateTime)
                .build();
    }


    public Map<DayOfWeek, Map.Entry<LocalTime, Integer>> findGreatestMeetingSlot(List<AvailabilityEntity> availabilities) {
        return AvailabilityProcessor.findGreatestMeetingSlot(availabilities, 1);
    }

    public List<AvailabilityEntity> getAvailabilityByUserId(Long id) {
        return availabilityRepository.findByUserId(id);
    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public List<AvailabilityEntity> getAllAvailabilities() {
        return availabilityRepository.findAll();
    }
}