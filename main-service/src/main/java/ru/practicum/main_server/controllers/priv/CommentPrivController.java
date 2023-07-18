package ru.practicum.main_server.controllers.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main_server.dtos.comment.CommentDto;
import ru.practicum.main_server.dtos.comment.NewCommentDto;
import ru.practicum.main_server.Services.Comment.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users/{userId}/comments")
public class CommentPrivController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createByPriv(@PathVariable Long userId,
                                   @RequestParam Long eventId,
                                   @Valid @RequestBody NewCommentDto newCommentDto) {
        return commentService.createByPriv(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateByPriv(@PathVariable Long userId,
                                   @PathVariable Long commentId,
                                   @Valid @RequestBody NewCommentDto newCommentDto) {
        return commentService.updateByPriv(userId, commentId, newCommentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByPriv(@PathVariable Long userId,
                             @PathVariable Long commentId) {
        commentService.deleteByPriv(userId, commentId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentsByPriv(
            @PathVariable Long userId,
            @RequestParam(required = false) Long eventId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        return commentService.getCommentsByPriv(userId, eventId, PageRequest.of(from / size, size));
    }
}