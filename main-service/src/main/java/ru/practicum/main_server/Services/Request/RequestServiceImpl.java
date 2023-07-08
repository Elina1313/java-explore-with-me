package ru.practicum.main_server.Services.Request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_server.dtos.request.RequestDto;
import ru.practicum.main_server.dtos.request.RequestStatusUpdateDto;
import ru.practicum.main_server.dtos.request.RequestStatusUpdateResult;
import ru.practicum.main_server.enums.EventState;
import ru.practicum.main_server.enums.UpdateRequestStatus;
import ru.practicum.main_server.enums.RequestStatus;
import ru.practicum.main_server.exceptions.*;
import ru.practicum.main_server.mappers.RequestMapper;
import ru.practicum.main_server.models.Event;
import ru.practicum.main_server.models.Request;
import ru.practicum.main_server.models.User;
import ru.practicum.main_server.repositories.EventRepository;
import ru.practicum.main_server.repositories.RequestRepository;
import ru.practicum.main_server.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public List<RequestDto> getRequestsByOwnerOfEvent(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotExistException("User is not exist");
        }
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        return requestMapper.toRequestDtoList(requestRepository.findAllByEventWithInitiator(userId, eventId));
    }

    @Override
    public RequestDto createRequest(Long userId, Long eventId) {
        User requester = userRepository.findById(userId).orElseThrow(() -> new UserNotExistException("User not found "
                + userId));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotExistException("Event not exist "
                + eventId));
        Request request = new Request(LocalDateTime.now(), event, requester, RequestStatus.PENDING);
        Optional<Request> requests = requestRepository.findByRequesterIdAndEventId(userId, eventId);
        if (requests.isPresent()) {
            throw new AlreadyExistsException("Unable to add a repeat request: userId {}, eventId {} " + userId + eventId);
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new AlreadyExistsException("The initiator of the event cannot add an application for participation in his event " + userId);
        }
        if (!(event.getState().equals(EventState.PUBLISHED))) {
            throw new AlreadyExistsException("Unable to participate in an unpublished event");
        }
        int limit = event.getParticipantLimit();
        if (limit != 0) {
            if (limit == event.getConfirmedRequests()) {
                throw new AlreadyExistsException("The number of applications for participation in the event has reached the limit: " + limit);
            }
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        if (!event.getRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        }
        Request savedRequest = requestRepository.save(request);
        return requestMapper.toRequestDto(savedRequest);
    }

    @Override
    public RequestDto cancelRequests(Long userId, Long requestId) {
        Request request = requestRepository.findByRequesterIdAndId(userId, requestId)
                .orElseThrow(() -> new RequestNotExistException(String.format("Request with id=%s was not found",
                        requestId)));
        request.setStatus(RequestStatus.CANCELED);
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Transactional(readOnly = true)
    @Override
    public List<RequestDto> getCurrentUserRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotExistException(String.format("User with id ", userId + "was not found"));
        }
        return requestMapper.toRequestDtoList(requestRepository.findAllByRequesterId(userId));
    }

    @Transactional
    @Override
    public RequestStatusUpdateResult updateRequests(Long userId, Long eventId,
                                                    RequestStatusUpdateDto requestStatusUpdateDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotExistException("Event is not exist"));
        RequestStatusUpdateResult result = new RequestStatusUpdateResult();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return result;
        }

        List<Request> requests = requestRepository.findAllByEventWithInitiator(userId, eventId);
        List<Request> requestsToUpdate = requests.stream().filter(x -> requestStatusUpdateDto.getRequestIds()
                .contains(x.getId())).collect(Collectors.toList());

        if (requestsToUpdate.stream().anyMatch(x -> x.getStatus().equals(RequestStatus.CONFIRMED) &&
                requestStatusUpdateDto.getStatus().equals(UpdateRequestStatus.REJECTED))) {
            throw new RequestAlreadyConfirmedException("Request already confirmed");
        }

        if (event.getConfirmedRequests() + requestsToUpdate.size() > event.getParticipantLimit() &&
                requestStatusUpdateDto.getStatus().equals(UpdateRequestStatus.CONFIRMED)) {
            throw new ParticipantLimitException("The limit of participants has been exceeded");
        }

        for (Request x : requestsToUpdate) {
            x.setStatus(RequestStatus.valueOf(requestStatusUpdateDto.getStatus().toString()));
        }

        requestRepository.saveAll(requestsToUpdate);

        if (requestStatusUpdateDto.getStatus().equals(UpdateRequestStatus.CONFIRMED)) {
            event.setConfirmedRequests(event.getConfirmedRequests() + requestsToUpdate.size());
        }

        eventRepository.save(event);

        if (requestStatusUpdateDto.getStatus().equals(UpdateRequestStatus.CONFIRMED)) {
            result.setConfirmedRequests(requestMapper.toRequestDtoList(requestsToUpdate));
        }

        if (requestStatusUpdateDto.getStatus().equals(UpdateRequestStatus.REJECTED)) {
            result.setRejectedRequests(requestMapper.toRequestDtoList(requestsToUpdate));
        }

        return result;
    }
}
