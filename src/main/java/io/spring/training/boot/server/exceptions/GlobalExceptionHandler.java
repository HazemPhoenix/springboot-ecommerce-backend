package io.spring.training.boot.server.exceptions;

import io.spring.training.boot.server.DTOs.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookNotFound(BookNotFoundException exception, WebRequest request){
        return formatErrorResponse(HttpStatus.NOT_FOUND, exception, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidData(MethodArgumentNotValidException exception, WebRequest request){
        return formatErrorResponse(HttpStatus.UNPROCESSABLE_CONTENT, exception, request);
    }

    private ResponseEntity<ErrorResponse> formatErrorResponse(HttpStatus status, Exception exception, WebRequest request){
        return new ResponseEntity<>(new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                request.getDescription(false)
        ), status);
    }
}
