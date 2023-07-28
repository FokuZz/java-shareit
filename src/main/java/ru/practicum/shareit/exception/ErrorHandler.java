package ru.practicum.shareit.exception;

import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, ValidationException.class,
            NullPointerException.class, ConversionFailedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(final RuntimeException e) {
        if (e.getMessage().contains("UNSUPPORTED_STATUS")) {
            return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS");
        }
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({AlreadyExist.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExist(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({JdbcSQLIntegrityConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> sqlValidation(final RuntimeException e) {
        if (e.getMessage().contains("[null]")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } else if (e.getMessage()
                .contains("PUBLIC.UK_6DOTKOTT2KJSP8VW4D0M25FB7_INDEX_4")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ErrorResponse("необработанная ошибка валидации  = " + e.getMessage()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return new ErrorResponse(e.getBindingResult().toString());
    }


}
