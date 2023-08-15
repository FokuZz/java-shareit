package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class ErrorHandler {


    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnsupportedStatusException(final RuntimeException e) {
        if (e.getMessage().contains("UNSUPPORTED_STATUS")) {
            log.error("Unknown state: UNSUPPORTED_STATUS");
            return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS");
        }
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingHeader(MissingRequestHeaderException ex) {
        String errorMessage = ex.getMessage();
        if (errorMessage.contains("X-Sharer-User-Id")) {
            log.error("Required request header 'X-Sharer-User-Id' is missing");
            return new ErrorResponse("Required request header 'X-Sharer-User-Id' is missing");
        }
        return new ErrorResponse(ex.getMessage());
    }


    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse(e.getBindingResult().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.error("Произошла непредвиденная ошибка: " + e.getMessage(), e);
        return new ErrorResponse("Произошла непредвиденная ошибка: " + e.getMessage());
    }

}
