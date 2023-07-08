package ru.practicum.main_server.controllers.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_server.Services.Event.EventService;
import ru.practicum.main_server.dtos.event.EventFullDto;
import ru.practicum.main_server.dtos.event.EventShortDto;
import ru.practicum.main_server.enums.EventSortValue;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventPubController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEventsWithParams(@RequestParam(name = "text", required = false) String text,
                                                   @RequestParam(name = "categories", required = false) List<Long> categories,
                                                   @RequestParam(name = "paid", required = false) Boolean paid,
                                                   @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                                   @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                                   @RequestParam(name = "onlyAvailable", required = false) boolean onlyAvailable,
                                                   @RequestParam(name = "sort", required = false) EventSortValue sort,
                                                   @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                                   @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                                                   HttpServletRequest request) {
        return eventService.getAllEventsByPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable Long id, HttpServletRequest request) {
        return eventService.getEvent(id, request);
    }
}
