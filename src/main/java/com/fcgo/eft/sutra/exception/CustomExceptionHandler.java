package com.fcgo.eft.sutra.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomExceptionHandler {

    @ResponseStatus(HttpStatus.PRECONDITION_REQUIRED)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MethodArgumentNotValid> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> list = new ArrayList<>();
        result.getFieldErrors().forEach(e ->
                list.add(FieldError.builder()
                        .name(e.getField())
                        .message(e.getDefaultMessage())
                        .build()));
        MethodArgumentNotValid error = MethodArgumentNotValid.builder()
                .message("validation error")
                .fieldErrors(list)
                .build();
        log.error(error.toString());
        return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).body(error);
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorMessage> handleUnauthorized(UnauthorizedException e) {
        log.error(e.getDto().getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getDto());
    }

    @ExceptionHandler(value = PermissionDeniedException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public ResponseEntity<ErrorMessage> permissionDeniedExceptionException(PermissionDeniedException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(e.getDto());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorMessage> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getSQLException().getMessage();
        log.error("Constraint Violation Exception {}", message);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorMessage.builder().message(message).code(502).build());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorMessage> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = Objects.requireNonNull(ex.getRootCause()).getMessage();

        log.error("Data integrity violation Exception {}", message);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorMessage.builder().message(message).code(1001).build());
    }

    @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
    @ResponseStatus(HttpStatus.INSUFFICIENT_STORAGE)
    public ResponseEntity<ErrorMessage> handleInvalidDataAccess(InvalidDataAccessResourceUsageException ex) {
        String message = Objects.requireNonNullElse(ex.getRootCause(), ex).getMessage();
        log.error("Invalid Data {}", message);
        return ResponseEntity.status(HttpStatus.INSUFFICIENT_STORAGE).body(ErrorMessage.builder().message(message).code(1002).build());
    }

    @ExceptionHandler(value = CustomException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorMessage> handleException(CustomException e) {
        log.error("CustomException {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getDto());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ResponseEntity<ErrorMessage> Exception(Exception e) {
        log.error("Exception {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(ErrorMessage.builder().message(e.getMessage()).code(1003).build());

    }

}
