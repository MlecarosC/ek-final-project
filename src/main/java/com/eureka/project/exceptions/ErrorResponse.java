package com.eureka.project.exceptions;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private LocalDate timestamp;
    private int code;
    private String message;
}
