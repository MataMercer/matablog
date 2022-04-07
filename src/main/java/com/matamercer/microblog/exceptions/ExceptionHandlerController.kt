package com.matamercer.microblog.exceptions

import lombok.extern.slf4j.Slf4j
import org.springframework.web.bind.annotation.ControllerAdvice

@ControllerAdvice
@Slf4j
class ExceptionHandlerController {
    //    @ExceptionHandler(Exception.class)
    //    public ModelAndView exception(HttpServletRequest request, Exception exception) {
    //        String uri = request.getRequestURI();
    //        log.error("Request page: {} raised exception {}", uri, exception);
    //
    //        ModelAndView model = new ModelAndView("error");
    //        model.addObject("error", Throwables.getRootCause(exception).getMessage());
    //        model.addObject("status", Throwables.getRootCause(exception).getCause());
    //        model.addObject("path", uri);
    //        model.addObject("message", exception.getMessage());
    //
    //        return model;
    //    }
    //    @ExceptionHandler({OAuth2UserRegistrationFailed.class})
    //    public ModelAndView oAuth2UserRegistrationFailed(HttpServletRequest request, Exception exception) {
    //        String uri = request.getRequestURI();
    //        log.error("Request page: {} raised exception {}", uri, exception);
    //
    //        ModelAndView model = new ModelAndView("registerOAuth2Failure");
    //        model.addObject("error", Throwables.getRootCause(exception).getMessage());
    //        model.addObject("status", Throwables.getRootCause(exception).getCause());
    //        model.addObject("path", uri);
    //        model.addObject("message", exception.getMessage());
    //
    //        return model;
    //    }
}