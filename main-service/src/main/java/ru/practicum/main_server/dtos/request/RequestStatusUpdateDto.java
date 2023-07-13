package ru.practicum.main_server.dtos.request;

import lombok.*;
import ru.practicum.main_server.enums.UpdateRequestStatus;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestStatusUpdateDto {
    private List<Long> requestIds;
    private UpdateRequestStatus status;
}
