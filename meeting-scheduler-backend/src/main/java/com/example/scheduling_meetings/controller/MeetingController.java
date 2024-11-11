//package com.example.scheduling_meetings.controller;
//
//import com.example.scheduling_meetings.domain.dto.AvailabilityDto;
//import com.example.scheduling_meetings.domain.dto.UserDto;
//import com.example.scheduling_meetings.domain.model.AvailabilityEntity;
//import com.example.scheduling_meetings.domain.model.UserEntity;
//import com.example.scheduling_meetings.mappers.Mapper;
//import com.example.scheduling_meetings.service.MeetingService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.DayOfWeek;
//import java.time.LocalTime;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@RequiredArgsConstructor
//@RestController
//@RequestMapping("/api/meetings")
//public class MeetingController {
//    private final MeetingService meetingService;
//    private final Mapper<UserEntity, UserDto> userMapper;
//    private final Mapper<AvailabilityEntity, AvailabilityDto> availabilityMapper;
//
//    @GetMapping(path = "/users")
//    public List<UserDto> listOfUsers() {
//        List<UserEntity> users = meetingService.getAllUsers();
//        return users.stream()
//                .map(userMapper::mapTo)
//                .collect(Collectors.toList());
//    }
//
//    @GetMapping(path = "/availabilities")
//    public List<AvailabilityDto> listOfAvailabilities() {
//        List<AvailabilityEntity> availabilities = meetingService.getAllAvailabilities();
//        return availabilities.stream()
//                .map(availabilityMapper::mapTo)
//                .collect(Collectors.toList());
//    }
//
//    @PostMapping(path = "/users")
//    public ResponseEntity<UserEntity> addUser(@RequestBody UserEntity user) {
//        UserEntity createdUser = meetingService.createUser(user);
//        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
//    }
//
//    @PostMapping(path = "/availabilities")
//    public ResponseEntity<AvailabilityDto> addAvailability(@RequestBody AvailabilityDto availability) {
//        AvailabilityDto addedAvailability = meetingService.createAvailability(availability);
//        return new ResponseEntity<>(addedAvailability, HttpStatus.CREATED);
//    }
//
//    @DeleteMapping(path = "/users/{id}")
//    public ResponseEntity<String> deleteUser(@PathVariable("id") String id) {
//        try {
//            meetingService.deleteUser(Long.parseLong(id));
//            return ResponseEntity.ok("User deleted successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//        }
//    }
//
//    @DeleteMapping(path = "/availabilities/{id}")
//    public ResponseEntity<String> deleteAvailability(@PathVariable("id") String id) {
//        try {
//            meetingService.deleteAvailability(Long.parseLong(id));
//            return ResponseEntity.ok("Availability deleted successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Availability not found");
//        }
//    }
//
//    @DeleteMapping(path = "/user-availabilities/{id}")
//    public ResponseEntity<String> deleteAvailabilitiesOfUser(@PathVariable("id") String id) {
//        try {
//            meetingService.deleteAvailabilitiesOfUser(Long.parseLong(id));
//            return ResponseEntity.ok("All availabilities for user deleted successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found or no availabilities to delete");
//        }
//    }
//
//    @DeleteMapping(path = "/users")
//    public ResponseEntity<String> deleteAllUsers() {
//        try {
//            meetingService.deleteAllUsers();
//            return ResponseEntity.ok("All users deleted successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Users not found");
//        }
//    }
//    @DeleteMapping(path = "/availabilities")
//    public ResponseEntity<String> deleteAllAvailabilities() {
//        try {
//            meetingService.deleteAllAvailabilities();
//            return ResponseEntity.ok("All availabilities deleted successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Availabilities not found");
//        }
//    }
//
//    @GetMapping("/schedule")
//    public ResponseEntity<Map<DayOfWeek, List<LocalTime>>> findMeeting(@RequestParam int durationHours) {
//        List<AvailabilityEntity> availabilities = meetingService.getAllAvailabilities();
//        Map<DayOfWeek, List<LocalTime>> availableSlots = meetingService.findAvailableMeetingSlots(availabilities, durationHours);
//        return new ResponseEntity<>(availableSlots, HttpStatus.OK);
//    }
//
//    @GetMapping("/greatest-common-slot")
//    public ResponseEntity<Map<DayOfWeek, Map.Entry<LocalTime, Integer>>> findGreatestCommonSlot() {
//        List<AvailabilityEntity> availabilities = meetingService.getAllAvailabilities();
//        Map<DayOfWeek, Map.Entry<LocalTime, Integer>> greatestCommonSlot = meetingService.findGreatestMeetingSlot(availabilities);
//        return new ResponseEntity<>(greatestCommonSlot, HttpStatus.OK);
//    }
//}

package com.example.scheduling_meetings.controller;

import com.example.scheduling_meetings.domain.dto.AvailabilityDto;
import com.example.scheduling_meetings.domain.dto.UserDto;
import com.example.scheduling_meetings.domain.model.AvailabilityEntity;
import com.example.scheduling_meetings.domain.model.UserEntity;
import com.example.scheduling_meetings.mappers.Mapper;
import com.example.scheduling_meetings.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/meetings")
public class MeetingController {
    private final MeetingService meetingService;
    private final Mapper<UserEntity, UserDto> userMapper;
    private final Mapper<AvailabilityEntity, AvailabilityDto> availabilityMapper;

    @GetMapping(path = "/users")
    public List<UserDto> listOfUsers() {
        List<UserEntity> users = meetingService.getAllUsers();
        return users.stream()
                .map(userMapper::mapTo)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/availabilities")
    public List<AvailabilityDto> listOfAvailabilities() {
        List<AvailabilityEntity> availabilities = meetingService.getAllAvailabilities();
        return availabilities.stream()
                .map(availabilityMapper::mapTo)
                .collect(Collectors.toList());
    }

    @PostMapping(path = "/users")
    public ResponseEntity<UserEntity> addUser(@RequestBody UserEntity user) {
        UserEntity createdUser = meetingService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PostMapping(path = "/availabilities")
    public ResponseEntity<AvailabilityDto> addAvailability(@RequestBody AvailabilityDto availability) {
        AvailabilityDto addedAvailability = meetingService.createAvailability(availability);
        return new ResponseEntity<>(addedAvailability, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") String id) {
        try {
            meetingService.deleteUser(Long.parseLong(id));
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @DeleteMapping(path = "/availabilities/{id}")
    public ResponseEntity<String> deleteAvailability(@PathVariable("id") String id) {
        try {
            meetingService.deleteAvailability(Long.parseLong(id));
            return ResponseEntity.ok("Availability deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Availability not found");
        }
    }

    @DeleteMapping(path = "/user-availabilities/{id}")
    public ResponseEntity<String> deleteAvailabilitiesOfUser(@PathVariable("id") String id) {
        try {
            meetingService.deleteAvailabilitiesOfUser(Long.parseLong(id));
            return ResponseEntity.ok("All availabilities for user deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found or no availabilities to delete");
        }
    }

    @DeleteMapping(path = "/users")
    public ResponseEntity<String> deleteAllUsers() {
        try {
            meetingService.deleteAllUsers();
            return ResponseEntity.ok("All users deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Users not found");
        }
    }

    @DeleteMapping(path = "/availabilities")
    public ResponseEntity<String> deleteAllAvailabilities() {
        try {
            meetingService.deleteAllAvailabilities();
            return ResponseEntity.ok("All availabilities deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Availabilities not found");
        }
    }

    @GetMapping("/schedule")
    public ResponseEntity<Map<DayOfWeek, List<LocalTime>>> findMeeting(@RequestParam int durationHours) {
        Map<DayOfWeek, List<LocalTime>> availableSlots = meetingService.findAvailableMeetingSlots(durationHours);
        return new ResponseEntity<>(availableSlots, HttpStatus.OK);
    }

    @GetMapping("/greatest-common-slot")
    public ResponseEntity<Map<DayOfWeek, Map.Entry<LocalTime, Integer>>> findGreatestCommonSlot() {
        List<AvailabilityEntity> availabilities = meetingService.getAllAvailabilities();
        Map<DayOfWeek, Map.Entry<LocalTime, Integer>> greatestCommonSlot = meetingService.findGreatestMeetingSlot(availabilities);
        return new ResponseEntity<>(greatestCommonSlot, HttpStatus.OK);
    }
}