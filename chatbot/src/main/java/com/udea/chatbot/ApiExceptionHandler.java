package com.udea.chatbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail invalidRequest(MethodArgumentNotValidException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "The message must not be empty and must be at most 2000 characters.");
    }

    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail assistantUnavailable(RuntimeException ex) {
        log.error("Chat request failed", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, "The assistant is temporarily unavailable. Please try again in a moment.");
    }
}
