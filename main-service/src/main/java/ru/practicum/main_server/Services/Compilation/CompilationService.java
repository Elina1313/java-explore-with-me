package ru.practicum.main_server.Services.Compilation;

import ru.practicum.main_server.dtos.compilation.CompilationDto;
import ru.practicum.main_server.dtos.compilation.NewCompilationDto;
import ru.practicum.main_server.dtos.compilation.UpdateCompilationRequestDto;

import java.util.List;

public interface CompilationService {
    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequestDto updateCompilationRequestDto);

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compId);
}
