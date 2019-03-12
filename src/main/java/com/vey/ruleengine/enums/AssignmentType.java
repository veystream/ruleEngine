package com.vey.ruleengine.enums;

/**
 * @Auther vey
 * @Date 2018/11/5
 */
public enum AssignmentType {
    STRING("String"),
    INTEGER("Integer"),
    LONG("Long"),
    DATE("Date");

    private String code;

    AssignmentType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static AssignmentType of(String code) {
        for (AssignmentType assignmentType : AssignmentType.values()) {
            if (assignmentType.getCode().equals(code)) {
                return assignmentType;
            }
        }
        return null;
    }
}
