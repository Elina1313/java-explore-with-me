package ru.practicum.mapper;

import ru.practicum.model.Stats;
import ru.practicum.EndpointHit;

import java.time.LocalDateTime;

public class StatsMapper {
    public static Stats endpointToStats(EndpointHit endpointHit, LocalDateTime timestamp) {
        return new Stats(null, endpointHit.getApp(), endpointHit.getUri(), endpointHit.getIp(), timestamp);
    }
}
