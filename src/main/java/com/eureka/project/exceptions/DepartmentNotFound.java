package com.eureka.project.exceptions;

public class DepartmentNotFound extends RuntimeException {
    public DepartmentNotFound(String message) {
        super(message);
    }   
}
