package com.ldb.lms.exception;

public class CourseRegistrationException extends RuntimeException {

    private String errorCode;

    public CourseRegistrationException(String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public CourseRegistrationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}