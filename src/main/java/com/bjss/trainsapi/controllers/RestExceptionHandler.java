package com.bjss.trainsapi.controllers;

import com.bjss.trainsapi.exceptions.EntityNotFoundException;
import com.bjss.trainsapi.model.dto.HandledExceptionResponseDto;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<Object>handleNotFound(Exception ex, WebRequest request) {
        return handleExceptionInternal(ex,
                HandledExceptionResponseDto.builder().error("Entity not found").message(ex.getLocalizedMessage()).build(),
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({ResponseStatusException.class})
    protected ResponseEntity<Object> handleResponseStatusException(Exception ex, WebRequest request) {
        return handleExceptionInternal(ex,
                HandledExceptionResponseDto.builder().error("Request Failed").message(ex.getLocalizedMessage()).build(),
                new HttpHeaders(), ((ResponseStatusException) ex).getStatus(), request);
    }

    @ExceptionHandler({ConstraintViolationException.class,
            DataIntegrityViolationException.class, IllegalArgumentException.class})
    public ResponseEntity<Object> handleBadRequest(
            Exception ex, WebRequest request) {
        return handleExceptionInternal(ex,
                HandledExceptionResponseDto.builder().error("Bad request").message(ex.getLocalizedMessage()).build(),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({IOException.class})
    public ResponseEntity<Object> handleMissingFiles(
            Exception ex, WebRequest request) {
        return handleExceptionInternal(ex,
                HandledExceptionResponseDto.builder().error("Missing required files.").message(ex.getLocalizedMessage()).build(),
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
