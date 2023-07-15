package ru.practicum.main_server.Services.Comment;

import org.springframework.data.domain.Pageable;
import ru.practicum.main_server.dtos.comment.CommentDto;
import ru.practicum.main_server.dtos.comment.NewCommentDto;

import java.util.List;

public interface CommentService {
    List<CommentDto> getCommentsByAdmin(Pageable pageable);

    void deleteByAdmin(Long commentId);

    List<CommentDto> getCommentsByPriv(Long userId, Long eventId, Pageable pageable);

    CommentDto createByPriv(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateByPriv(Long userId, Long commentId, NewCommentDto newCommentDto);

    void deleteByPriv(Long userId, Long commentId);

    List<CommentDto> getCommentsByPub(Long eventId, Pageable pageable);

    CommentDto getCommentByPub(Long commentId);
}
