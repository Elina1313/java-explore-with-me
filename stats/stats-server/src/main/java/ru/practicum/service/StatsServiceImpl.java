package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public void addHit(EndpointHit endpointHit) {
        statsRepository.save(StatsMapper.endpointToStats(endpointHit,
                LocalDateTime.parse(endpointHit.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return statsRepository.getAllStatsDistinctIp(start, end);
            } else {
                return statsRepository.getAllStats(start, end);
            }
        } else {
            if (unique) {
                return statsRepository.getStatsByUrisDistinctIp(start, end, uris);
            } else {
                return statsRepository.getStatsByUris(start, end, uris);
            }
        }
    }
}