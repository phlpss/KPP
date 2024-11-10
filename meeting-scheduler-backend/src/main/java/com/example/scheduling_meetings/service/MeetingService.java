package com.example.scheduling_meetings.service;

import com.example.scheduling_meetings.domain.dto.AvailabilityDto;
import com.example.scheduling_meetings.domain.model.AvailabilityEntity;
import com.example.scheduling_meetings.domain.model.UserEntity;
import com.example.scheduling_meetings.mappers.Mapper;
import com.example.scheduling_meetings.repository.AvailabilityRepository;
import com.example.scheduling_meetings.repository.UserRepository;
import com.example.scheduling_meetings.util.AvailabilityProcessor;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class MeetingService {

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