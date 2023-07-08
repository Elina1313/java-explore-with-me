package ru.practicum.main_server.controllers.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_server.Services.Event.EventService;
import ru.practicum.main_server.Services.Request.RequestService;
import ru.practicum.main_server.dtos.event.EventFullDto;
import ru.practicum.main_server.dtos.event.EventShortDto;
import ru.practicum.main_server.dtos.event.NewEventDto;
import ru.practicum.main_server.dtos.event.UpdateEventUserRequest;
import ru.practicum.main_server.dtos.request.RequestDto;
import ru.practicum.main_server.dtos.request.RequestStatusUpdateDto;
import ru.practicum.main_server.dtos.request.RequestStatusUpdateResult;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventPrivController {
    private final EventService eventService;

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId, @Valid @RequestBody NewEventDto newEventDto) {
        return eventService.createEvent(userId, newEventDto);
    }

    @GetMapping
    public List<EventShortDto> getEventsByUser(@PathVariable Long userId,
                                               @RequestParam(name = "from", defaultValue = "0",
                                                       required = false) Integer from,
                                               @RequestParam(name = "size", defaultValue = "10",
                                                       required = false) Integer size) {
        return eventService.getEvents(userId, from, size);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getRequestsByOwnerOfEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return requestService.getRequestsByOwnerOfEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestStatusUpdateResult updateRequests(@PathVariable Long userId, @PathVariable Long eventId,
                                                    @RequestBody RequestStatusUpdateDto requestStatusUpdateDto) {
        return requestService.updateRequests(userId, eventId, requestStatusUpdateDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByUser(@PathVariable Long userId,
                                          @PathVariable Long eventId,
                                          @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        return eventService.updateEventByUser(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByUser(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getEventByUser(userId, eventId);
    }
}
