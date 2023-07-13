package ru.practicum.main_server.controllers.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_server.Services.Compilation.CompilationService;
import ru.practicum.main_server.dtos.compilation.CompilationDto;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/compilations")
public class CompilationPubController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(name = "pinned", required = false) Boolean pinned,
                                                @RequestParam(name = "from", required = false,
                                                        defaultValue = "0") Integer from,
                                                @RequestParam(name = "size", required = false,
                                                        defaultValue = "10") Integer size) {
        return compilationService.getCompilations(pinned, PageRequest.of(from / size, size));
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        return compilationService.getCompilationById(compId);
    }
}
