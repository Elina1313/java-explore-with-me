package ru.practicum.main_server.Services.Event;

import ru.practicum.main_server.dtos.event.*;
import ru.practicum.main_server.enums.EventSortValue;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getEvents(Long userId, Integer from, Integer size);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    EventFullDto getEventByUser(Long userId, Long eventId);

    List<EventFullDto> getAllEventsByAdmin(List<Long> userIds, List<String> states, List<Long> categories,
                                           String rangeStart, String rangeEnd, Integer from, Integer size,
                                           HttpServletRequest request);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> getAllEventsByPublic(String text, List<Long> categoriesIds, Boolean paid, String rangeStart,
                                             String rangeEnd, Boolean onlyAvailable, EventSortValue sort, Integer from,
                                             Integer size, HttpServletRequest request);

    EventFullDto getEvent(Long id, HttpServletRequest request);
}
