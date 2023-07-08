package ru.practicum.main_server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main_server.models.Compilation;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {
}
