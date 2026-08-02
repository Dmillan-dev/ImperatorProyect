package imperator.api.errors;

import imperator.application.exceptions.ApplicationException;
import imperator.application.exceptions.AuthorizationException;
import imperator.application.exceptions.BusinessRuleViolationException;
import imperator.application.exceptions.ConflictException;
import imperator.application.exceptions.NotFoundException;
import imperator.application.exceptions.ValidationException;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public final class ApiExceptionHandler {

    @ExceptionHandler(ApiContractException.class)
    ResponseEntity<ApiErrorResponse> handleApiContract(
            ApiContractException exception,
            HttpServletRequest request
    ) {
        return response(exception.status(), exception.code(), exception.getMessage(), request);
    }

    @ExceptionHandler(AuthorizationException.class)
    ResponseEntity<ApiErrorResponse> handleAuthorization(
            AuthorizationException exception,
            HttpServletRequest request
    ) {
        return applicationResponse(HttpStatus.FORBIDDEN, exception, request);
    }

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ApiErrorResponse> handleApplicationNotFound(
            NotFoundException exception,
            HttpServletRequest request
    ) {
        return applicationResponse(HttpStatus.NOT_FOUND, exception, request);
    }

    @ExceptionHandler(ConflictException.class)
    ResponseEntity<ApiErrorResponse> handleConflict(
            ConflictException exception,
            HttpServletRequest request
    ) {
        return applicationResponse(HttpStatus.CONFLICT, exception, request);
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    ResponseEntity<ApiErrorResponse> handleBusinessRule(
            BusinessRuleViolationException exception,
            HttpServletRequest request
    ) {
        if ("BUSINESS_VALUE_METADATA_INVALID".equals(exception.code())) {
            return response(ApiErrorCode.INTERNAL_SERVER_ERROR, request);
        }
        return applicationResponse(HttpStatus.CONFLICT, exception, request);
    }

    @ExceptionHandler(ValidationException.class)
    ResponseEntity<ApiErrorResponse> handleValidation(
            ValidationException exception,
            HttpServletRequest request
    ) {
        if ("INVALID_PAGINATION".equals(exception.code())) {
            return response(HttpStatus.BAD_REQUEST, exception.code(), exception.getMessage(), request);
        }
        return applicationResponse(HttpStatus.valueOf(422), exception, request);
    }

    @ExceptionHandler(NotImplementedApiException.class)
    ResponseEntity<ApiErrorResponse> handleNotImplemented(
            HttpServletRequest request
    ) {
        return response(ApiErrorCode.NOT_IMPLEMENTED, request);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            ServletRequestBindingException.class,
            IllegalArgumentException.class
    })
    ResponseEntity<ApiErrorResponse> handleBadRequest(
            HttpServletRequest request
    ) {
        return response(ApiErrorCode.BAD_REQUEST, request);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    ResponseEntity<ApiErrorResponse> handleUnsupportedMediaType(
            HttpServletRequest request
    ) {
        return response(ApiErrorCode.UNSUPPORTED_MEDIA_TYPE, request);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    ResponseEntity<ApiErrorResponse> handleNotAcceptable(
            HttpServletRequest request
    ) {
        return response(ApiErrorCode.NOT_ACCEPTABLE, request);
    }

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ApiErrorResponse> handleResponseStatus(
            ResponseStatusException exception,
            HttpServletRequest request
    ) {
        return response(ApiErrorCode.fromStatus(exception.getStatusCode().value()), request);
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    ResponseEntity<ApiErrorResponse> handleNotFound(
            HttpServletRequest request
    ) {
        return response(ApiErrorCode.RESOURCE_NOT_FOUND, request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ApiErrorResponse> handleMethodNotAllowed(
            HttpServletRequest request
    ) {
        return response(ApiErrorCode.METHOD_NOT_ALLOWED, request);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiErrorResponse> handleUnexpected(
            Exception exception,
            HttpServletRequest request
    ) {
        if (hasSqlCause(exception)) {
            return response(ApiErrorCode.SERVICE_UNAVAILABLE, request);
        }
        return response(ApiErrorCode.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ApiErrorResponse> applicationResponse(
            HttpStatus status,
            ApplicationException exception,
            HttpServletRequest request
    ) {
        return response(status, exception.code(), exception.getMessage(), request);
    }

    private ResponseEntity<ApiErrorResponse> response(
            ApiErrorCode errorCode,
            HttpServletRequest request
    ) {
        return response(errorCode.status(), errorCode.name(), errorCode.message(), request);
    }

    private ResponseEntity<ApiErrorResponse> response(
            HttpStatus status,
            String code,
            String message,
            HttpServletRequest request
    ) {
        String correlationId = CorrelationIdFilter.currentCorrelationId(request);
        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .header(CorrelationIdFilter.HEADER_NAME, correlationId)
                .body(new ApiErrorResponse(code, message, correlationId, java.util.Map.of()));
    }

    private boolean hasSqlCause(Throwable exception) {
        Throwable current = exception;
        while (current != null) {
            if (current instanceof java.sql.SQLException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
