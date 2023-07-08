package ru.practicum.main_server.exceptions;

public class NameAlreadyExistException extends RuntimeException{
    public NameAlreadyExistException(String message){
        super(message);
    }
}
