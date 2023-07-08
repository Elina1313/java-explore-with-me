package ru.practicum.main_server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main_server.models.Location;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByLatAndLon(Float lat, Float lon);
}
