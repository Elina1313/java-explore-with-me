package ru.practicum.main_server.Services.Compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.main_server.dtos.compilation.CompilationDto;
import ru.practicum.main_server.dtos.compilation.NewCompilationDto;
import ru.practicum.main_server.dtos.compilation.UpdateCompilationRequestDto;
import ru.practicum.main_server.exceptions.CompilationNotExistException;
import ru.practicum.main_server.mappers.CompilationMapper;
import ru.practicum.main_server.models.Compilation;
import ru.practicum.main_server.models.Event;
import ru.practicum.main_server.repositories.CompilationRepository;
import ru.practicum.main_server.repositories.EventRepository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final EventRepository eventRepository;
    private final EntityManager entityManager;
    private final CompilationRepository compilationRepository;
    private final CompilationMapper mapper;

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
        Compilation compilation = new Compilation();
        compilation.setEvents(new HashSet<>(events));
        compilation.setPinned(newCompilationDto.getPinned());
        compilation.setTitle(newCompilationDto.getTitle());

        Compilation savedCompilation = compilationRepository.save(compilation);
        log.debug("Compilation is created");
        return mapper.mapToCompilationDto(savedCompilation);
    }

    @Override
    public void deleteCompilation(Long compId) {
        compilationRepository.deleteById(compId);
        log.debug("Compilation with ID = {} is deleted", compId);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequestDto updateCompilationRequestDto) {
        Compilation oldCompilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotExistException("Can't update compilation - " +
                        "the compilation doesn't exist"));
        List<Long> eventsIds = updateCompilationRequestDto.getEvents();
        if (eventsIds != null) {
            List<Event> events = eventRepository.findAllByIdIn(updateCompilationRequestDto.getEvents());
            oldCompilation.setEvents(new HashSet<>(events));
        }
        if (updateCompilationRequestDto.getPinned() != null) {
            oldCompilation.setPinned(updateCompilationRequestDto.getPinned());
        }
        if (updateCompilationRequestDto.getTitle() != null) {
            oldCompilation.setTitle(updateCompilationRequestDto.getTitle());
        }
        Compilation updatedCompilation = compilationRepository.save(oldCompilation);
        log.debug("Compilation with ID = {} is updated", compId);
        return mapper.mapToCompilationDto(updatedCompilation);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Pageable pageable) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Compilation> query = builder.createQuery(Compilation.class);

        Root<Compilation> root = query.from(Compilation.class);
        Predicate criteria = builder.conjunction();

        if (pinned != null) {
            Predicate isPinned;
            if (pinned) {
                isPinned = builder.isTrue(root.get("pinned"));
            } else {
                isPinned = builder.isFalse(root.get("pinned"));
            }
            criteria = builder.and(criteria, isPinned);
        }

        query.select(root).where(criteria);
        List<Compilation> compilations = entityManager.createQuery(query)
                .setFirstResult(pageable.getPageNumber())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        return mapper.mapToListCompilationDto(compilations);
    }

    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotExistException("Compilation doesn't exist"));
        return mapper.mapToCompilationDto(compilation);
    }

}
