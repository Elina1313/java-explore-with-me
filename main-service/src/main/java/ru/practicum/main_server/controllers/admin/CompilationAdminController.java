package ru.practicum.main_server.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_server.Services.Compilation.CompilationService;
import ru.practicum.main_server.dtos.compilation.CompilationDto;
import ru.practicum.main_server.dtos.compilation.NewCompilationDto;
import ru.practicum.main_server.dtos.compilation.UpdateCompilationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/admin/compilations")
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        return compilationService.addCompilation(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@NotNull @PathVariable Long compId,
                                            @Valid @RequestBody UpdateCompilationRequestDto updateCompilationRequestDto) {
        return compilationService.updateCompilation(compId, updateCompilationRequestDto);
    }
}
