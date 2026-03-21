package com.example.mychat.exception;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase manejador de excepciones
 *
 * @author @cperez
 */
@ControllerAdvice
@RestController
@Slf4j
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    private ResponseEntity<Object> buildErrorResponse(
            Exception ex,
            WebRequest request,
            HttpStatus status,
            String customMessage,
            List<FieldValidationError> errors) {

        log.error("Exception: {}", ex.getClass().getName(), ex);

        String path = request.getDescription(false).replace("uri=", "");
        String method = ((ServletWebRequest) request).getRequest().getMethod();

        StandardError error = StandardError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(customMessage != null ? customMessage : ex.getMessage())
                .path(path)
                .method(method)
                .errors(errors != null ? errors : new ArrayList<>())
                .build();

        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public final ResponseEntity<Object> authenticationExceptionHandler(BadCredentialsException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, "Invalid credentials", null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<Object> authenticationExceptionHandler(AccessDeniedException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.UNAUTHORIZED, ex.getMessage(), null);
    }

    @ExceptionHandler(ModelNotFoundException.class)
    public final ResponseEntity<Object> modelNotFoundExceptionHandler(ModelNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(JwtServiceException.class)
    public final ResponseEntity<Object> jwtServiceExceptionHandler(JwtServiceException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null);
    }

    @ExceptionHandler(BusinessLogicException.class)
    public final ResponseEntity<Object> businessLogicExceptionHandler(BusinessLogicException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(NullPointerException.class)
    public final ResponseEntity<Object> nullPointerExceptionHandler(NullPointerException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, "Null Exception", null);
    }


    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> exceptionHandler(Exception ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, "Json structure is not valid", null);

    }

    @Override
    public ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getMessage(), null);

    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<FieldValidationError> fieldErrors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> fieldErrors.add(new FieldValidationError(
                err.getField(),
                err.getDefaultMessage()
        )));

        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, "Request validation failed", fieldErrors);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, "Method Not Supported", null);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, "No Handler Found", null);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }
}