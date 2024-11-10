package com.example.scheduling_meetings.domain.model;

import com.example.scheduling_meetings.util.ZoneIdConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    private Long id;
    private String name;

    @Convert(converter = ZoneIdConverter.class)
    private ZoneId timeZone;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvailabilityEntity> availabilities = new ArrayList<>();
}