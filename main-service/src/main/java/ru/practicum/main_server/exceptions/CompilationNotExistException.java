package ru.practicum.main_server.exceptions;

public class CompilationNotExistException extends RuntimeException {
    public CompilationNotExistException(String message) {
        super(message);
    }
}
