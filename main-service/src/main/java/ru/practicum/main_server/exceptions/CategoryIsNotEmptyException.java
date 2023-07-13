package ru.practicum.main_server.exceptions;

public class CategoryIsNotEmptyException extends RuntimeException {
    public CategoryIsNotEmptyException(String message) {
        super(message);
    }
}
