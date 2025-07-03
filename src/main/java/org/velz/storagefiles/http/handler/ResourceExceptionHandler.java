package org.velz.storagefiles.http.handler;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.velz.storagefiles.exception.DirectoryNotExistsException;
import org.velz.storagefiles.exception.FileAlreadyExistsException;
import org.velz.storagefiles.exception.InvalidFileException;
import org.velz.storagefiles.exception.StorageException;

import java.nio.file.InvalidPathException;
import java.util.Map;

@RestControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(InvalidPathException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException() {
        return Map.of("message", "невалидный или отсутствующий путь");
    }

    @ExceptionHandler(StorageException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleStorageException() {
        return Map.of("message", "ресурс не найден");
    }

    @ExceptionHandler(InvalidFileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidFileException() {
        return Map.of("message", "невалидное тело запроса");
    }

    @ExceptionHandler(FileAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleFileAlreadyExistsException() {
        return Map.of("message", "файл уже существует");
    }

    @ExceptionHandler(DirectoryNotExistsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleDirectoryNotExistsException() {
        return Map.of("message", "папка не существует");
    }
}
