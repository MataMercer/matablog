package com.matamercer.microblog.web.error;

import com.matamercer.microblog.utilities.GenericResponse;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    public RestResponseEntityExceptionHandler() {
        super();
    }

    // API

    // 400
    @Override
    protected ResponseEntity<Object> handleBindException(final BindException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.error("400 Status Code", ex);
        final BindingResult result = ex.getBindingResult();
        final GenericResponse bodyOfResponse = new GenericResponse(result.getAllErrors(), "Invalid" + result.getObjectName());
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.error("400 Status Code", ex);
        final BindingResult result = ex.getBindingResult();
        final GenericResponse bodyOfResponse = new GenericResponse(result.getAllErrors(), "Invalid" + result.getObjectName());
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    //    @ExceptionHandler({ InvalidOldPasswordException.class })
//    public ResponseEntity<Object> handleInvalidOldPassword(final RuntimeException ex, final WebRequest request) {
//        logger.error("400 Status Code", ex);
//        final GenericResponse bodyOfResponse = new GenericResponse(messageSource.getMessage("message.invalidOldPassword", null, request.getLocale()), "InvalidOldPassword");
//        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
//    }
//
//    @ExceptionHandler({ ReCaptchaInvalidException.class })
//    public ResponseEntity<Object> handleReCaptchaInvalid(final RuntimeException ex, final WebRequest request) {
//        logger.error("400 Status Code", ex);
//        final GenericResponse bodyOfResponse = new GenericResponse(messageSource.getMessage("message.invalidReCaptcha", null, request.getLocale()), "InvalidReCaptcha");
//        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
//    }

    //401
    @ExceptionHandler({ExpiredJwtException.class})
    public ResponseEntity<Object> handleExpiredAccessToken(final RuntimeException ex, final WebRequest request) {
        logger.error("401 Status Code", ex);
        final GenericResponse bodyOfResponse = new GenericResponse(messageSource.getMessage("message.expiredAccessToken", null, request.getLocale()), "ExpiredAccessToken");
        var headers = new HttpHeaders();
        headers.add("WWW-Authenticate", "Bearer");
        return handleExceptionInternal(ex, bodyOfResponse, headers, HttpStatus.UNAUTHORIZED, request);
    }



    // 403
    @ExceptionHandler({RevokedRefreshTokenException.class})
    public ResponseEntity<Object> handleRevokedRefreshToken(final RuntimeException ex, final WebRequest request) {
        logger.error("403 Status Code", ex);
        final GenericResponse bodyOfResponse = new GenericResponse(messageSource.getMessage("message.revokedRefreshToken", null, request.getLocale()), "RevokedRefreshToken");
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    // 404
    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<Object> handleUserNotFound(final RuntimeException ex, final WebRequest request) {
        logger.error("404 Status Code", ex);
        final GenericResponse bodyOfResponse = new GenericResponse(messageSource.getMessage("message.userNotFound", null, request.getLocale()), "UserNotFound");
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    // 409
    @ExceptionHandler({UserAlreadyExistsException.class})
    public ResponseEntity<Object> handleUserAlreadyExist(final RuntimeException ex, final WebRequest request) {
        logger.error("409 Status Code", ex);
        final GenericResponse bodyOfResponse = new GenericResponse(messageSource.getMessage("message.regError", null, request.getLocale()), "UserAlreadyExist");
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    // 500
    @ExceptionHandler({MailAuthenticationException.class})
    public ResponseEntity<Object> handleMail(final RuntimeException ex, final WebRequest request) {
        logger.error("500 Status Code", ex);
        final GenericResponse bodyOfResponse = new GenericResponse(messageSource.getMessage("message.email.config.error", null, request.getLocale()), "MailError");
        return new ResponseEntity<>(bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
//
//    @ExceptionHandler({ ReCaptchaUnavailableException.class })
//    public ResponseEntity<Object> handleReCaptchaUnavailable(final RuntimeException ex, final WebRequest request) {
//        logger.error("500 Status Code", ex);
//        final GenericResponse bodyOfResponse = new GenericResponse(messageSource.getMessage("message.unavailableReCaptcha", null, request.getLocale()), "InvalidReCaptcha");
//        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
//    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleInternal(final RuntimeException ex, final WebRequest request) {
        logger.error("500 Status Code", ex);
        final GenericResponse bodyOfResponse = new GenericResponse(messageSource.getMessage("message.error", null, request.getLocale()), "InternalError");
        return new ResponseEntity<>(bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
