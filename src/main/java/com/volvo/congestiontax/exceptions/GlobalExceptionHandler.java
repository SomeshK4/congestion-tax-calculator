package com.volvo.congestiontax.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.URI;

/**
 * @author Somesh Kumar
 */
@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(CityNotSupportedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleRequestParameters(CityNotSupportedException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setType(URI.create("https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/404"));
        problemDetail.setTitle("City/Country not found");
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;

    }

    @ExceptionHandler(VehicleTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleRequestParameters(VehicleTypeNotSupportedException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setType(URI.create("https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/404"));
        problemDetail.setTitle("VehicleType not found");
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;

    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleGlobalException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Server Error");
        problemDetail.setType(URI.create("https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/500"));
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }

}
