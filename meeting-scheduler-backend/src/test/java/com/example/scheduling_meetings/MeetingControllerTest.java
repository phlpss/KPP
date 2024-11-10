package com.example.scheduling_meetings;

import com.example.scheduling_meetings.controller.MeetingController;
import com.example.scheduling_meetings.domain.dto.AvailabilityDto;
import com.example.scheduling_meetings.domain.dto.UserDto;
import com.example.scheduling_meetings.domain.model.AvailabilityEntity;
import com.example.scheduling_meetings.domain.model.UserEntity;
import com.example.scheduling_meetings.mappers.Mapper;
import com.example.scheduling_meetings.service.MeetingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MeetingControllerTest {

    @InjectMocks
    private MeetingController meetingController;

    @Mock
    private MeetingService meetingService;

    @Mock
    private Mapper<UserEntity, UserDto> userMapper;

    @Mock
    private Mapper<AvailabilityEntity, AvailabilityDto> availabilityMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // todo: fix test
    @Test
    void listOfUsers_ShouldReturnListOfUserDtos() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        when(meetingService.getAllUsers()).thenReturn(Collections.singletonList(userEntity));

        UserDto userDto = new UserDto();
        when(userMapper.mapTo(userEntity)).thenReturn(userDto);

        // Act
        List<UserDto> result = meetingController.listOfUsers();

        // Assert
        assertEquals(1, result.size());
        verify(meetingService, times(1)).getAllUsers();
        verify(userMapper, times(1)).mapTo(userEntity);
    }

    // todo: fix test
    @Test
    void listOfAvailabilities_ShouldReturnListOfAvailabilityDtos() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        AvailabilityEntity availabilityEntity = new AvailabilityEntity();
        availabilityEntity.setId(2L);
        availabilityEntity.setUser(userEntity);
        when(meetingService.getAllAvailabilities()).thenReturn(Collections.singletonList(availabilityEntity));

        AvailabilityDto availabilityDto = new AvailabilityDto();
        when(availabilityMapper.mapTo(availabilityEntity)).thenReturn(availabilityDto);

        List<AvailabilityDto> result = meetingController.listOfAvailabilities();

        assertEquals(1, result.size());
        verify(meetingService, times(1)).getAllAvailabilities();
        verify(availabilityMapper, times(1)).mapTo(availabilityEntity);
    }

    @Test
    void addUser_ShouldReturnCreatedUser() {
        UserEntity user = new UserEntity();
        when(meetingService.createUser(any(UserEntity.class))).thenReturn(user);

        ResponseEntity<UserEntity> response = meetingController.addUser(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(meetingService, times(1)).createUser(user);
    }

    @Test
    void addAvailability_ShouldReturnCreatedAvailability() {
        AvailabilityDto availability = new AvailabilityDto();
        when(meetingService.createAvailability(any(AvailabilityDto.class))).thenReturn(availability);

        ResponseEntity<AvailabilityDto> response = meetingController.addAvailability(availability);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(availability, response.getBody());
        verify(meetingService, times(1)).createAvailability(availability);
    }

    @Test
    void deleteUser_ShouldReturnOkStatus() {
        doNothing().when(meetingService).deleteUser(anyLong());

        ResponseEntity<String> response = meetingController.deleteUser("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(meetingService, times(1)).deleteUser(1L);
    }

    @Test
    void deleteAvailability_ShouldReturnOkStatus() {
        doNothing().when(meetingService).deleteAvailability(anyLong());

        ResponseEntity<String> response = meetingController.deleteAvailability("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(meetingService, times(1)).deleteAvailability(1L);
    }

    @Test
    void deleteAvailabilitiesOfUser_ShouldReturnOkStatus() {
        doNothing().when(meetingService).deleteAvailabilitiesOfUser(anyLong());

        ResponseEntity<String> response = meetingController.deleteAvailabilitiesOfUser("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(meetingService, times(1)).deleteAvailabilitiesOfUser(1L);
    }

    @Test
    void deleteAllUsers_ShouldReturnOkStatus() {
        doNothing().when(meetingService).deleteAllUsers();

        ResponseEntity<String> response = meetingController.deleteAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(meetingService, times(1)).deleteAllUsers();
    }

    @Test
    void deleteAllAvailabilities_ShouldReturnOkStatus() {
        doNothing().when(meetingService).deleteAllAvailabilities();

        ResponseEntity<String> response = meetingController.deleteAllAvailabilities();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(meetingService, times(1)).deleteAllAvailabilities();
    }

    @Test
    void findMeeting_ShouldReturnAvailableMeetingSlots() {
        AvailabilityEntity availability = new AvailabilityEntity();
        int durationHours = 1;
        List<LocalTime> slots = Arrays.asList(LocalTime.of(9, 0), LocalTime.of(10, 0));

        when(meetingService.getAvailabilityByUserId(anyLong())).thenReturn(Collections.singletonList(availability));
        when(meetingService.findAvailableMeetingSlots(anyList(), eq(durationHours))).thenReturn((Map<DayOfWeek, List<LocalTime>>) slots);

        ResponseEntity<Map<DayOfWeek, List<LocalTime>>> response = meetingController.findMeeting(durationHours);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(slots, response.getBody());
        verify(meetingService, times(1)).findAvailableMeetingSlots(anyList(), eq(durationHours));
    }
}
