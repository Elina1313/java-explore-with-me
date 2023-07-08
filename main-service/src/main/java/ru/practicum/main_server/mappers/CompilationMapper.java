package ru.practicum.main_server.mappers;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.main_server.dtos.compilation.CompilationDto;
import ru.practicum.main_server.models.Compilation;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface CompilationMapper {

    CompilationDto mapToCompilationDto(Compilation savedCompilation);

    List<CompilationDto> mapToListCompilationDto(List<Compilation> compilations);

}
