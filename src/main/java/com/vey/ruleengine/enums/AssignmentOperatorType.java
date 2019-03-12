package com.vey.ruleengine.enums;

/**
 * @Auther vey
 * @Date 2018/11/5
 */
public enum AssignmentOperatorType {
    /**
     * 多个等值匹配，只需满足其中一个，operand有任意个操作数，包括一个
     */
    IN("in", 1),

    /**
     * 闭区间（范围匹配），operand有且有两个操作数
     */
    BETWEEN("bt", 2),

    /**
     * 大于等于（范围匹配），operand仅支持一个操作数
     */
    GREAT_THAN("gt", 1),

    /**
     * 小于等于（范围匹配），operand仅支持一个操作数
     */
    LESS_THAN("lt", 1);

    private String code;
    private int size;

    AssignmentOperatorType(String code, int size) {
        this.code = code;
        this.size = size;
    }

    public String getCode() {
        return code;
    }

    public int getSize() {
        return size;
    }

    public static AssignmentOperatorType of(String code) {
        for (AssignmentOperatorType assignmentOperatorType : AssignmentOperatorType.values()) {
            if (assignmentOperatorType.getCode().equals(code)) {
                return assignmentOperatorType;
            }
        }
        return null;
    }

}
