package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ErrorHandlerTest {

    @Test
    public void testHandleNotFoundException() {
        // Arrange
        ErrorHandler errorHandler = new ErrorHandler();
        NotFoundException exception = new NotFoundException("Not found");

        // Act
        ErrorResponse response = errorHandler.handleNotFoundException(exception);

        // Assert
        assertEquals("Not found", response.getError());
    }

    @Test
    public void testHandleIllegalArgumentException() {
        // Arrange
        ErrorHandler errorHandler = new ErrorHandler();
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        // Act
        ErrorResponse response = errorHandler.handleIllegalArgumentException(exception);

        // Assert
        assertEquals("Invalid argument", response.getError());
    }

    @Test
    public void testHandleIllegalArgumentExceptionWithUnsupportedStatus() {
        // Arrange
        ErrorHandler errorHandler = new ErrorHandler();
        IllegalArgumentException exception = new IllegalArgumentException("Unsupported status: UNSUPPORTED_STATUS");

        // Act
        ErrorResponse response = errorHandler.handleIllegalArgumentException(exception);

        // Assert
        assertEquals("Unknown state: UNSUPPORTED_STATUS", response.getError());
    }

    @Test
    public void testHandleUnsupportedStatusException() {
        // Arrange
        ErrorHandler errorHandler = new ErrorHandler();
        UnsupportedStatusException exception = new UnsupportedStatusException("Unsupported status");

        // Act
        ErrorResponse response = errorHandler.handleUnsupportedStatusException(exception);

        // Assert
        assertEquals("Unknown state: UNSUPPORTED_STATUS", response.getError());
    }

    @Test
    public void testHandleAlreadyExist() {
        // Arrange
        ErrorHandler errorHandler = new ErrorHandler();
        AlreadyExist exception = new AlreadyExist("Already exists");

        // Act
        ErrorResponse response = errorHandler.handleAlreadyExist(exception);

        // Assert
        assertEquals("Already exists", response.getError());
    }

    @Test
    public void testSqlValidationWithNull() {
        // Arrange
        ErrorHandler errorHandler = new ErrorHandler();
        AlreadyExist exception = new AlreadyExist("[null]");

        // Act
        ResponseEntity<ErrorResponse> responseEntity = errorHandler.sqlValidation(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody().getError().contains("[null]"));
    }

    @Test
    public void testSqlValidationWithConflict() {
        // Arrange
        ErrorHandler errorHandler = new ErrorHandler();
        RuntimeException exception = new RuntimeException("PUBLIC.UK_6DOTKOTT2KJSP8VW4D0M25FB7_INDEX_4");

        // Act
        ResponseEntity<ErrorResponse> responseEntity = errorHandler.sqlValidation(exception);

        // Assert
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody().getError().contains("PUBLIC.UK_6DOTKOTT2KJSP8VW4D0M25FB7_INDEX_4"));
    }

    @Test
    public void testSqlValidationWithOtherError() {
        // Arrange
        ErrorHandler errorHandler = new ErrorHandler();
        RuntimeException exception = new RuntimeException("Some other error");

        // Act
        ResponseEntity<ErrorResponse> responseEntity = errorHandler.sqlValidation(exception);

        // Assert
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody().getError().contains("необработанная ошибка валидации"));
    }

}
