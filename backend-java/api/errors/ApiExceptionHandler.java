package imperator.api.errors;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public final class ApiExceptionHandler {

    @ExceptionHandler(NotImplementedApiException.class)
    ResponseEntity<ApiErrorResponse> handleNotImplemented(
            HttpServletRequest request
    ) {
        return response(ApiErrorCode.NOT_IMPLEMENTED, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiErrorResponse> handleBadRequest(
            HttpServletRequest request
    ) {
        return response(ApiErrorCode.BAD_REQUEST, request);
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
            HttpServletRequest request
    ) {
        return response(ApiErrorCode.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ApiErrorResponse> response(
            ApiErrorCode errorCode,
            HttpServletRequest request
    ) {
        String correlationId = CorrelationIdFilter.currentCorrelationId(request);
        return ResponseEntity
                .status(errorCode.status())
                .contentType(MediaType.APPLICATION_JSON)
                .header(CorrelationIdFilter.HEADER_NAME, correlationId)
                .body(ApiErrorResponse.from(errorCode, correlationId));
    }
}
