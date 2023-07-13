package ru.practicum.main_server.Services.Compilation;

import org.springframework.data.domain.Pageable;
import ru.practicum.main_server.dtos.compilation.CompilationDto;
import ru.practicum.main_server.dtos.compilation.NewCompilationDto;
import ru.practicum.main_server.dtos.compilation.UpdateCompilationRequestDto;

import java.util.List;

public interface CompilationService {
    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequestDto updateCompilationRequestDto);

    List<CompilationDto> getCompilations(Boolean pinned, Pageable pageable);

    CompilationDto getCompilationById(Long compId);
}
