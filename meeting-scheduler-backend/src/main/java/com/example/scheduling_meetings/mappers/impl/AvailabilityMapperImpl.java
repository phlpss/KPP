package com.example.scheduling_meetings.mappers.impl;

import com.example.scheduling_meetings.domain.dto.AvailabilityDto;
import com.example.scheduling_meetings.domain.model.AvailabilityEntity;
import com.example.scheduling_meetings.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

// https://cursos.alura.com.br/forum/topico-erro-com-modelmapper-could-not-autowire-no-beans-of-modelmapper-type-found-266091
@Component
class AvailabilityMapperImpl implements Mapper<AvailabilityEntity, AvailabilityDto> {
    private ModelMapper modelMapper;

    public AvailabilityMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public AvailabilityDto mapTo(AvailabilityEntity availabilityEntity) {
        return modelMapper.map(availabilityEntity, AvailabilityDto.class);
    }

    @Override
    public AvailabilityEntity mapFrom(AvailabilityDto availabilityDto) {
        return modelMapper.map(availabilityDto, AvailabilityEntity.class);
    }
}
