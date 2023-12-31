package ru.practicum.main_server.Services.Event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_server.dtos.event.*;
import ru.practicum.main_server.enums.*;
import ru.practicum.main_server.exceptions.*;
import ru.practicum.main_server.models.*;
import ru.practicum.main_server.repositories.*;
import ru.practicum.main_server.mappers.EventMapper;
import ru.practicum.stats_client.StatsClient;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final StatsClient statsClient;
    private final LocationRepository locationRepository;
    private final String datePattern = Pattern.DATE;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(datePattern);

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotExistException("User with id: " + userId + " is not exist"));
        checkEventTime(newEventDto.getEventDate());
        Event eventToSave = eventMapper.toEventModel(newEventDto);
        eventToSave.setState(EventState.PENDING);
        eventToSave.setConfirmedRequests(0L);
        eventToSave.setCreatedOn(LocalDateTime.now());

        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new CategoryNotExistException("Category is not exist"));
        eventToSave.setCategory(category);
        eventToSave.setInitiator(user);
        Event saved = eventRepository.save(eventToSave);
        return eventMapper.toEventFullDto(saved);
    }

    private void checkEventTime(LocalDateTime start) {
        if (start.isBefore(LocalDateTime.now())) {
            throw new EventValidationException("The start date of the event must be no earlier than an hour from the date of publication");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getEvents(Long userId, Pageable pageable) {
        return eventMapper.toEventShortDtoList(eventRepository.findAllByInitiatorId(userId, pageable).toList());
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {

        Event eventToUpdate = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotExistException("Event with id: " + eventId + " is not exist"));
        if (updateEventAdminRequest.getEventDate() != null) {
            checkEventTime(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction() == AdminState.PUBLISH_EVENT) {
                if (eventToUpdate.getState().equals(EventState.PENDING)) {
                    eventToUpdate.setState(EventState.PUBLISHED);
                    eventToUpdate.setPublishedOn(LocalDateTime.now());
                } else {
                    throw new AlreadyPublishedException("Event can be published only if it is in the waiting state for publication" +
                            updateEventAdminRequest.getStateAction());
                }
            }
            if (updateEventAdminRequest.getStateAction() == AdminState.REJECT_EVENT) {
                if (eventToUpdate.getState().equals(EventState.PUBLISHED)) {
                    throw new AlreadyPublishedException("Event can be rejected only if it has not been published yet" +
                            updateEventAdminRequest.getStateAction());
                }
                eventToUpdate.setState(EventState.CANCELED);
            }
        }
        updateEventEntity(updateEventAdminRequest, eventToUpdate);

        return eventMapper.toEventFullDto(eventRepository.save(eventToUpdate));
    }

    @Override
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {

        Event eventFromDb = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotExistException("Event with id: " + eventId + " is not exist"));
        if (eventFromDb.getState().equals(EventState.CANCELED) || eventFromDb.getState().equals(EventState.PENDING)) {
            if (updateEventUserRequest.getEventDate() != null && updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new EventValidationException("The date and time at which the event is scheduled cannot be earlier, " +
                        "less than two hours from now");
            }
            if (UserState.SEND_TO_REVIEW == updateEventUserRequest.getStateAction()) {
                eventFromDb.setState(EventState.PENDING);
            }
            if (UserState.CANCEL_REVIEW == updateEventUserRequest.getStateAction()) {
                eventFromDb.setState(EventState.CANCELED);
            }
        } else {
            throw new AlreadyPublishedException("You can change canceled events or events in the state of " +
                    "waiting for moderation, the status of the event = " + eventFromDb.getState());
        }

        updateEventEntity(updateEventUserRequest, eventFromDb);
        eventRepository.save(eventFromDb);
        return eventMapper.toEventFullDto(eventFromDb);
    }

    private void updateEventEntity(UpdateEventAdminRequest event, Event eventToUpdate) {
        eventToUpdate.setAnnotation(Objects.requireNonNullElse(event.getAnnotation(), eventToUpdate.getAnnotation()));
        eventToUpdate.setCategory(event.getCategory() == null
                ? eventToUpdate.getCategory()
                : categoryRepository.findById(event.getCategory()).orElseThrow(() -> new CategoryNotExistException("Category is not exist")));
        eventToUpdate.setDescription(Objects.requireNonNullElse(event.getDescription(), eventToUpdate.getDescription()));
        eventToUpdate.setEventDate(Objects.requireNonNullElse(event.getEventDate(), eventToUpdate.getEventDate()));
        eventToUpdate.setLocation(event.getLocation() == null
                ? eventToUpdate.getLocation()
                : locationRepository.findByLatAndLon(event.getLocation().getLat(), event.getLocation().getLon())
                .orElse(new Location(null, event.getLocation().getLat(), event.getLocation().getLon())));
        eventToUpdate.setPaid(Objects.requireNonNullElse(event.getPaid(), eventToUpdate.getPaid()));
        eventToUpdate.setParticipantLimit(Objects.requireNonNullElse(event.getParticipantLimit(), eventToUpdate.getParticipantLimit()));
        eventToUpdate.setRequestModeration(Objects.requireNonNullElse(event.getRequestModeration(), eventToUpdate.getRequestModeration()));
        eventToUpdate.setTitle(Objects.requireNonNullElse(event.getTitle(), eventToUpdate.getTitle()));
    }

    private void updateEventEntity(UpdateEventUserRequest event, Event eventToUpdate) {
        eventToUpdate.setAnnotation(Objects.requireNonNullElse(event.getAnnotation(), eventToUpdate.getAnnotation()));
        eventToUpdate.setCategory(event.getCategory() == null
                ? eventToUpdate.getCategory()
                : categoryRepository.findById(event.getCategory()).orElseThrow(() -> new CategoryNotExistException("Category is not exist")));
        eventToUpdate.setDescription(Objects.requireNonNullElse(event.getDescription(), eventToUpdate.getDescription()));
        eventToUpdate.setEventDate(Objects.requireNonNullElse(event.getEventDate(), eventToUpdate.getEventDate()));
        eventToUpdate.setLocation(event.getLocation() == null
                ? eventToUpdate.getLocation()
                : locationRepository.findByLatAndLon(event.getLocation().getLat(), event.getLocation().getLon())
                .orElse(new Location(null, event.getLocation().getLat(), event.getLocation().getLon())));
        eventToUpdate.setPaid(Objects.requireNonNullElse(event.getPaid(), eventToUpdate.getPaid()));
        eventToUpdate.setParticipantLimit(Objects.requireNonNullElse(event.getParticipantLimit(), eventToUpdate.getParticipantLimit()));
        eventToUpdate.setRequestModeration(Objects.requireNonNullElse(event.getRequestModeration(), eventToUpdate.getRequestModeration()));
        eventToUpdate.setTitle(Objects.requireNonNullElse(event.getTitle(), eventToUpdate.getTitle()));
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getEventByUser(Long userId, Long eventId) {
        return eventMapper.toEventFullDto(eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventNotExistException("Event is not exist")));
    }

    @Override
    public List<EventFullDto> getAllEventsByAdmin(List<Long> userIds, List<String> states, List<Long> categories,
                                                  String rangeStart, String rangeEnd, Pageable pageable,
                                                  HttpServletRequest request) {

        if (states == null & rangeStart == null & rangeEnd == null) {
            return eventRepository.findAll(pageable)
                    .stream()
                    .map(eventMapper::toEventFullDto)
                    .collect(Collectors.toList());
        }

        List<EventState> stateList = states.stream().map(EventState::valueOf).collect(Collectors.toList());

        LocalDateTime start;
        if (rangeStart != null && !rangeStart.isEmpty()) {
            start = LocalDateTime.parse(rangeStart, dateFormatter);
        } else {
            start = LocalDateTime.now().plusYears(5);
        }

        LocalDateTime end;
        if (rangeEnd != null && !rangeEnd.isEmpty()) {
            end = LocalDateTime.parse(rangeEnd, dateFormatter);
        } else {
            end = LocalDateTime.now().plusYears(5);
        }

        if (userIds.size() != 0 && states.size() != 0 && categories.size() != 0) {
            return findEventDtos(userIds, categories, pageable, stateList, start, end);
        }
        if (userIds.size() == 0 && categories.size() != 0) {
            return findEventDtos(userIds, categories, pageable, stateList, start, end);
        } else {
            return new ArrayList<>();
        }
    }

    private List<EventFullDto> findEventDtos(List<Long> userIds, List<Long> categories,
                                             Pageable pageable, List<EventState> stateList,
                                             LocalDateTime start, LocalDateTime end) {
        Page<Event> eventsWithPage = eventRepository.findAllWithAllParameters(userIds, stateList, categories, start, end,
                pageable);
        Set<Long> eventIds = eventsWithPage.stream().map(Event::getId).collect(Collectors.toSet());
        Map<Long, Long> viewStatsMap = statsClient.getSetViews(eventIds);

        List<EventFullDto> events = eventsWithPage.stream().map(eventMapper::toEventFullDto).collect(Collectors.toList());
        events.forEach(eventFullDto ->
                eventFullDto.setViews(viewStatsMap.getOrDefault(eventFullDto.getId(), 0L)));
        return events;
    }

    @Override
    public List<EventShortDto> getAllEventsByPublic(String text, List<Long> categories, Boolean paid, String rangeStart,
                                                    String rangeEnd, Boolean onlyAvailable, EventSortValue sort,
                                                    Pageable pageable, HttpServletRequest request) {

        LocalDateTime start = null;
        LocalDateTime end = null;

        if (rangeStart != null && rangeEnd != null) {
            start = LocalDateTime.parse(rangeStart, dateFormatter);
            end = LocalDateTime.parse(rangeEnd, dateFormatter);
            if (start.isAfter(end)) {
                throw new EventValidationException("Wrong dates");
            }
        } else {
            if (rangeStart == null && rangeEnd == null) {
                start = LocalDateTime.now();
                end = LocalDateTime.now().plusYears(10);
            } else {
                if (rangeStart == null) {
                    start = LocalDateTime.now();
                }
                if (rangeEnd == null) {
                    end = LocalDateTime.now();
                }
            }
        }
        final Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(EventSortValue.EVENT_DATE.equals(sort) ? "eventDate" : "views"));
        List<Event> eventEntities = eventRepository.searchPublishedEvents(categories, paid, start, end, pageRequest)
                .getContent();
        statsClient.addHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());

        if (eventEntities.isEmpty()) {
            return Collections.emptyList();
        }
        java.util.function.Predicate<Event> eventEntityPredicate;
        if (text != null && !text.isEmpty()) {
            eventEntityPredicate = eventEntity -> eventEntity.getAnnotation().toLowerCase().contains(text.toLowerCase())
                    || eventEntity.getDescription().toLowerCase().contains(text.toLowerCase());
        } else {
            eventEntityPredicate = eventEntity -> true;
        }

        Set<Long> eventIds = eventEntities.stream().filter(eventEntityPredicate).map(Event::getId).collect(Collectors.toSet());
        Map<Long, Long> viewStatsMap = statsClient.getSetViews(eventIds);

        List<EventShortDto> events = eventEntities.stream().map(eventMapper::toEventShortDto).collect(Collectors.toList());
        events.forEach(eventShortDto ->
                eventShortDto.setViews(viewStatsMap.getOrDefault(eventShortDto.getId(), 0L)));
        return events;

    }

    @Override
    public EventFullDto getEvent(Long eventId, HttpServletRequest request) {

        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new EventNotExistException("Event with id: " + eventId + " is not exist"));

        statsClient.addHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());

        Long views = statsClient.getStatistics(eventId);

        EventFullDto eventDto = eventMapper.toEventFullDto(event);
        eventDto.setViews(views);

        return eventDto;
    }

    @Override
    public Event getEventById(Long eventId) {

        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotExistException("Event with id " + eventId + " is not found."));
    }

}
