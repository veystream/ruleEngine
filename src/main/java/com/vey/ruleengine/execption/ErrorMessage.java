package com.vey.ruleengine.execption;

/**
 * @Auther vey
 * @Date 2018/11/7
 */
public enum ErrorMessage {
    ILLEGAL_ARGUMENTS("illegal arguments"),
    ASSIGNMENT_OPERATOR_INVALID("assignment operator invalid"),
    INTERNAL_ERROR("internal error")
    ;

    private String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
