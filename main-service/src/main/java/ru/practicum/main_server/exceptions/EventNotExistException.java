package ru.practicum.main_server.exceptions;

public class EventNotExistException extends RuntimeException{
    public EventNotExistException(String message){
            super(message);
        }
}
