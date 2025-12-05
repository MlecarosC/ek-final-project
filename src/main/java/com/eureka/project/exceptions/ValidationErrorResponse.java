package com.eureka.project.exceptions;

import java.time.LocalDate;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> validationErrors;

    public ValidationErrorResponse(LocalDate timestamp, int code, String message, Map<String, String> validationErrors) {
        super(timestamp, code, message);
        this.validationErrors = validationErrors;
    }
}
