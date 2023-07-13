package ru.practicum.main_server.exceptions;

public class CategoryNameAlreadyExistException extends RuntimeException {
    public CategoryNameAlreadyExistException(String message) {
        super(message);
    }
}
