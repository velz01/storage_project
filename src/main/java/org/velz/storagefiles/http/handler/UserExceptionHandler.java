package org.velz.storagefiles.http.handler;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.velz.storagefiles.exception.UserAlreadyExistsException;
import org.velz.storagefiles.exception.UserNotAuthorizedException;

import java.util.Map;


@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException() {
        return Map.of("message", "Ошибка валидации");
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String,String> handleUserNotFoundException() {
        return Map.of("message", "такого пользователя нет");
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String,String> handleUserAlreadyExistsException() {
        return Map.of("message", "username занят");
    }

    @ExceptionHandler(UserNotAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String,String> handleUserNotAuthorizedException() {
        return Map.of("message", "пользователь не авторизован");
    }

//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public Map<String,String> handleOtherExceptions() {
//        return Map.of("message", "неизвестная ошибка");
//    }


}
