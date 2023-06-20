package ru.practicum;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EndpointHit {

    //private Long id;
    //Идентификатор записи

    @NotBlank
    private String app;
    //example: ewm-main-service
    //Идентификатор сервиса для которого записывается информация

    @NotBlank
    private String uri;
    //example: /events/1
    //URI для которого был осуществлен запрос

    @NotBlank
    private String ip;
    //example: 192.163.0.1
    //IP-адрес пользователя, осуществившего запрос

    @NotBlank
    private String timestamp;
    //example: 2022-09-06 11:00:23
    //Дата и время, когда был совершен запрос к эндпоинту (в формате "yyyy-MM-dd HH:mm:ss")
}
