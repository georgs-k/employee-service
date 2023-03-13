package com.emansy.employeeservice.business.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorModel> handle(ConstraintViolationException ex, HttpServletRequest request) {
        ErrorModel errorModel = new ErrorModel(LocalDateTime.now(), 400, "Bad Request",
                ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorModel> handle(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        ErrorModel errorModel = new ErrorModel(LocalDateTime.now(), 400, "Bad Request",
                "Wrong format: a positive integer number is required", request.getRequestURI());
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }
}
