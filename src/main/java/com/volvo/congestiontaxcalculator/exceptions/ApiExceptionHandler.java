package com.volvo.congestiontaxcalculator.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.ProblemHandling;

import java.net.URI;
import java.util.UUID;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler implements ProblemHandling {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Problem> handleExceptions(final Exception exception, final NativeWebRequest request) {
        Problem problem = Problem.builder()
                .withTitle(Status.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .withStatus(Status.INTERNAL_SERVER_ERROR)
                .withInstance(createProblemInstanceUUID())
                .build();
        String errorMsg = String.format("Server Error : %s", problem);
        log.error(errorMsg);
        return create(exception, problem, request);
    }

    private URI createProblemInstanceUUID() {
        return URI.create("urn:uuid:"+ UUID.randomUUID());
    }
}
