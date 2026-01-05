package io.spring.training.boot.server.exceptions;

import io.spring.training.boot.server.DTOs.ErrorResponse;
import org.apache.coyote.Response;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException exception, WebRequest request){
        return formatErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidData(MethodArgumentNotValidException exception, WebRequest request){
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        StringBuilder messageBuilder = new StringBuilder();
        errors.forEach((name, msg) -> {
            messageBuilder.append(name).append(": ").append(msg).append(", ");
        });
        String message = messageBuilder.substring(0, messageBuilder.lastIndexOf(","));

        return formatErrorResponse(HttpStatus.UNPROCESSABLE_CONTENT, message, request);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ErrorResponse> handleStorageExceptions(StorageException exception, WebRequest request){
        return formatErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), request);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException exception, WebRequest request){
        return formatErrorResponse(HttpStatus.CONFLICT, exception.getMessage(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException exception, WebRequest request){
        return formatErrorResponse(HttpStatus.CONFLICT, exception.getMostSpecificCause().getMessage(), request);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStockException(InsufficientStockException exception, WebRequest request) {
        return formatErrorResponse(HttpStatus.CONFLICT, exception.getMessage(), request);
    }

    private ResponseEntity<ErrorResponse> formatErrorResponse(HttpStatus status, String message, WebRequest request){
        return new ResponseEntity<>(new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getDescription(false)
        ), status);
    }
}
