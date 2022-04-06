package com.matamercer.microblog.web.error.handler

import com.matamercer.microblog.utilities.GenericResponse
import com.matamercer.microblog.web.error.exceptions.*
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mail.MailAuthenticationException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import javax.validation.ConstraintViolationException

@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {
    @Autowired
    private lateinit var messageSource: MessageSource

    // API
    // 400
    protected override fun handleBindException(
        ex: org.springframework.validation.BindException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        logger.error("400 Status Code", ex)
        val result: BindingResult = ex.bindingResult
        val bodyOfResponse: GenericResponse = GenericResponse(result.allErrors, "Invalid" + result.objectName)
        return handleExceptionInternal(ex, bodyOfResponse, HttpHeaders(), HttpStatus.BAD_REQUEST, request)
    }

    protected override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        logger.error("400 Status Code", ex)
        val result: BindingResult = ex.bindingResult
        val bodyOfResponse: GenericResponse = GenericResponse(result.allErrors, "Invalid" + result.objectName)
        return handleExceptionInternal(ex, bodyOfResponse, HttpHeaders(), HttpStatus.BAD_REQUEST, request)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(ex: RuntimeException?, request: WebRequest): ResponseEntity<Any> {
        logger.error("400 Status Code", ex)
        val bodyOfResponse: GenericResponse =
            GenericResponse(messageSource.getMessage("auth.message.accessDenied", null, request.locale), "AccessDenied")
        return handleExceptionInternal(ex, bodyOfResponse, HttpHeaders(), HttpStatus.BAD_REQUEST, request)
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
    @ExceptionHandler(ExpiredJwtException::class)
    fun handleExpiredAccessToken(ex: RuntimeException?, request: WebRequest): ResponseEntity<Any> {
        logger.error("401 Status Code", ex)
        val bodyOfResponse: GenericResponse = GenericResponse(
            messageSource.getMessage("message.expiredAccessToken", null, request.locale),
            "ExpiredAccessToken"
        )
        val headers = HttpHeaders()
        headers.add("WWW-Authenticate", "Bearer")
        return handleExceptionInternal(ex, bodyOfResponse, headers, HttpStatus.UNAUTHORIZED, request)
    }

    // 403
    @ExceptionHandler(RevokedRefreshTokenException::class)
    fun handleRevokedRefreshToken(ex: RuntimeException?, request: WebRequest): ResponseEntity<Any> {
        logger.error("403 Status Code", ex)
        val bodyOfResponse: GenericResponse = GenericResponse(
            messageSource.getMessage("message.revokedRefreshToken", null, request.locale),
            "RevokedRefreshToken"
        )
        return handleExceptionInternal(ex, bodyOfResponse, HttpHeaders(), HttpStatus.FORBIDDEN, request)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: RuntimeException?, request: WebRequest): ResponseEntity<Any> {
        logger.error("403 Status Code", ex)
        val bodyOfResponse: GenericResponse =
            GenericResponse(messageSource.getMessage("auth.message.accessDenied", null, request.locale), "AccessDenied")
        return handleExceptionInternal(ex, bodyOfResponse, HttpHeaders(), HttpStatus.FORBIDDEN, request)
    }

    // 404
    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(ex: RuntimeException?, request: WebRequest): ResponseEntity<Any> {
        logger.error("404 Status Code", ex)
        val bodyOfResponse: GenericResponse =
            GenericResponse(messageSource.getMessage("message.userNotFound", null, request.locale), "UserNotFound")
        return handleExceptionInternal(ex, bodyOfResponse, HttpHeaders(), HttpStatus.NOT_FOUND, request)
    }

    // 409
    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExist(ex: RuntimeException?, request: WebRequest): ResponseEntity<Any> {
        logger.error("409 Status Code", ex)
        val bodyOfResponse: GenericResponse =
            GenericResponse(messageSource.getMessage("message.regError", null, request.locale), "UserAlreadyExist")
        return handleExceptionInternal(ex, bodyOfResponse, HttpHeaders(), HttpStatus.CONFLICT, request)
    }

    @ExceptionHandler(AlreadyExistsException::class)
    fun handleResourceAlreadyExist(ex: RuntimeException?, request: WebRequest): ResponseEntity<Any> {
        logger.error("409 Status Code", ex)
        val bodyOfResponse: GenericResponse =
            GenericResponse(messageSource.getMessage("message.regError", null, request.locale), "ResourceAlreadyExist")
        return handleExceptionInternal(ex, bodyOfResponse, HttpHeaders(), HttpStatus.CONFLICT, request)
    }

    // 500
    @ExceptionHandler(MailAuthenticationException::class)
    fun handleMail(ex: RuntimeException?, request: WebRequest): ResponseEntity<Any> {
        logger.error("500 Status Code", ex)
        val bodyOfResponse: GenericResponse =
            GenericResponse(messageSource.getMessage("message.email.config.error", null, request.locale), "MailError")
        return ResponseEntity(bodyOfResponse, HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    //
    //    @ExceptionHandler({ ReCaptchaUnavailableException.class })
    //    public ResponseEntity<Object> handleReCaptchaUnavailable(final RuntimeException ex, final WebRequest request) {
    //        logger.error("500 Status Code", ex);
    //        final GenericResponse bodyOfResponse = new GenericResponse(messageSource.getMessage("message.unavailableReCaptcha", null, request.getLocale()), "InvalidReCaptcha");
    //        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    //    }
    //    @ExceptionHandler({Exception.class})
    //    public ResponseEntity<Object> handleInternal(final RuntimeException ex, final WebRequest request) {
    //        logger.error("500 Status Code", ex);
    //        final GenericResponse bodyOfResponse = new GenericResponse(messageSource.getMessage("message.error", null, request.getLocale()), "InternalError");
    //        return new ResponseEntity<>(bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    //    }
}