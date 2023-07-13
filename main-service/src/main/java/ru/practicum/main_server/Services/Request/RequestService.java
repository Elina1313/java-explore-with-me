package ru.practicum.main_server.Services.Request;

import ru.practicum.main_server.dtos.request.RequestDto;
import ru.practicum.main_server.dtos.request.RequestStatusUpdateDto;
import ru.practicum.main_server.dtos.request.RequestStatusUpdateResult;

import java.util.List;

public interface RequestService {

    List<RequestDto> getCurrentUserRequests(Long userId);

    RequestDto createRequest(Long userId, Long eventId);

    RequestDto cancelRequests(Long userId, Long requestId);

    List<RequestDto> getRequestsByOwnerOfEvent(Long userId, Long eventId);

    RequestStatusUpdateResult updateRequests(Long userId, Long eventId,
                                             RequestStatusUpdateDto requestStatusUpdateDto);

}
