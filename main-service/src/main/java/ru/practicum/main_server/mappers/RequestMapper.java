package ru.practicum.main_server.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.main_server.dtos.request.RequestDto;
import ru.practicum.main_server.models.Request;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface RequestMapper {

    List<RequestDto> toRequestDtoList(List<Request> allByEventWithInitiator);

    @Mapping(source = "event.id", target = "event")
    @Mapping(source = "requester.id", target = "requester")
    RequestDto toRequestDto(Request save);
}
