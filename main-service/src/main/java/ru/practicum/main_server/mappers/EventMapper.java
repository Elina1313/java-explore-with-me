package ru.practicum.main_server.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.main_server.dtos.event.EventFullDto;
import ru.practicum.main_server.dtos.event.EventShortDto;
import ru.practicum.main_server.dtos.event.NewEventDto;
import ru.practicum.main_server.models.Event;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(source = "category", target = "category.id")
    Event toEventModel(NewEventDto newEventDto);

    List<EventShortDto> toEventShortDtoList(List<Event> events);

    EventFullDto toEventFullDto(Event save);

    EventShortDto toEventShortDto(Event event);
}
