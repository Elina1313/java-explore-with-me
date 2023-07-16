package ru.practicum.main_server.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.main_server.dtos.comment.CommentDto;
import ru.practicum.main_server.models.Comment;

@Component
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {

    @Mapping(target = "id", source = "event.id")
    CommentDto toCommentDto(Comment comment);
}
