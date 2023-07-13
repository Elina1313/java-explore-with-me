package ru.practicum.main_server.Services.Comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_server.Services.Event.EventService;
import ru.practicum.main_server.Services.User.UserService;
import ru.practicum.main_server.dtos.comment.CommentDto;
import ru.practicum.main_server.dtos.comment.NewCommentDto;
import ru.practicum.main_server.enums.EventState;
import ru.practicum.main_server.exceptions.ForbiddenException;
import ru.practicum.main_server.exceptions.CommentNotFoundException;
import ru.practicum.main_server.mappers.CommentMapper;
import ru.practicum.main_server.models.Comment;
import ru.practicum.main_server.models.Event;
import ru.practicum.main_server.models.User;
import ru.practicum.main_server.repositories.CommentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final UserService userService;
    private final EventService eventService;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public List<CommentDto> getCommentsByAdmin(Pageable pageable) {
        return toCommentsDto(commentRepository.findAll(pageable).toList());
    }

    @Override
    @Transactional
    public void deleteByAdmin(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getCommentsByPriv(Long userId, Long eventId, Pageable pageable) {
        userService.getUserById(userId);

        List<Comment> comments;
        if (eventId != null) {
            eventService.getEventById(eventId);

            comments = commentRepository.findAllByAuthorIdAndEventId(userId, eventId);
        } else {
            comments = commentRepository.findAllByAuthorId(userId);
        }

        return toCommentsDto(comments);
    }

    @Override
    @Transactional
    public CommentDto createByPriv(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenException("Event is not published.");
        }

        Comment comment = Comment.builder()
                .text(newCommentDto.getText())
                .author(user)
                .event(event)
                .createdOn(LocalDateTime.now())
                .build();

        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentDto updateByPriv(Long userId, Long commentId, NewCommentDto newCommentDto) {
        userService.getUserById(userId);

        Comment commentFromRepository = getCommentById(commentId);

        checkUserIsOwner(userId, commentFromRepository.getAuthor().getId());

        commentFromRepository.setText(newCommentDto.getText());
        commentFromRepository.setEditedOn(LocalDateTime.now());

        return commentMapper.toCommentDto(commentRepository.save(commentFromRepository));
    }

    @Override
    @Transactional
    public void deleteByPriv(Long userId, Long commentId) {
        userService.getUserById(userId);

        checkUserIsOwner(userId, getCommentById(commentId).getAuthor().getId());

        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getCommentsByPub(Long eventId, Pageable pageable) {
        eventService.getEventById(eventId);

        return toCommentsDto(commentRepository.findAllByEventId(eventId, pageable));
    }

    @Override
    public CommentDto getCommentByPub(Long commentId) {
        return commentMapper.toCommentDto(getCommentById(commentId));
    }

    private List<CommentDto> toCommentsDto(List<Comment> comments) {
        return comments.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment with ID: " + commentId + "is not found."));
    }

    private void checkUserIsOwner(Long id, Long userId) {
        if (!Objects.equals(id, userId)) {
            throw new ForbiddenException("User is not an owner.");
        }
    }
}
