package ru.practicum;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ViewStats {
    private String app;
    //example: ewm-main-service
    //Название сервиса

    private String uri;
    //example: /events/1
    //URI сервиса
    private Long hits;
    //Количество просмотров
}
