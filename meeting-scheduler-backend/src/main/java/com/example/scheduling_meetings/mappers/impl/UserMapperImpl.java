package com.example.scheduling_meetings.mappers.impl;

import com.example.scheduling_meetings.domain.dto.UserDto;
import com.example.scheduling_meetings.domain.model.UserEntity;
import com.example.scheduling_meetings.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements Mapper<UserEntity, UserDto> {

    private ModelMapper modelMapper;

    public UserMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDto mapTo(UserEntity authorEntity) {
        return modelMapper.map(authorEntity, UserDto.class);
    }

    @Override
    public UserEntity mapFrom(UserDto authorDto) {
        return modelMapper.map(authorDto, UserEntity.class);
    }
}