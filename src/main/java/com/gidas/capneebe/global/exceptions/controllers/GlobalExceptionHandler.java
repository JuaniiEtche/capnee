package com.gidas.capneebe.global.exceptions.controllers;

import com.gidas.capneebe.global.config.swagger.responses.ErrorResponse;
import com.gidas.capneebe.global.exceptions.customs.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> processRuntimeException(Exception ex, HttpServletRequest request) {
        ResponseEntity.BodyBuilder builder;
        ErrorResponse errorResponse;
        builder = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
        errorResponse = ErrorResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return builder.body(errorResponse);
    }

    @ExceptionHandler(DuplicateEntryException.class)
    public ResponseEntity<ErrorResponse> DuplicateEntryException(DuplicateEntryException ex, HttpServletRequest request) {
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(HttpStatus.CONFLICT);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.CONFLICT.value())
                .status(HttpStatus.CONFLICT.getReasonPhrase())
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return builder.body(errorResponse);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<ErrorResponse> notFoundExceptionHandler(NotFoundException ex, HttpServletRequest request) {
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(HttpStatus.NOT_FOUND);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .status(HttpStatus.NOT_FOUND.getReasonPhrase())
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return builder.body(errorResponse);
    }


    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> badRequestExceptionHandler(BadRequestException ex, HttpServletRequest request) {
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(HttpStatus.BAD_REQUEST);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return builder.body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> badCredentialsExceptionHandler(BadCredentialsException ex, HttpServletRequest request) {
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(HttpStatus.UNAUTHORIZED);
        ErrorResponse errorResponses = ErrorResponse.builder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .status(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return builder.body(errorResponses);
    }

}
