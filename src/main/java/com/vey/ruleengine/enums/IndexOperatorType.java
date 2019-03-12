package com.vey.ruleengine.enums;

/**
 * @Auther xiwenwan
 * @Date 2018/11/7
 */
public enum IndexOperatorType {
    EQUAL("equal", "等于"),
    GREAT_THAN("greatThan", "大于"),
    LESS_THAN("lessThan", "小于"),
    GREAT_THAN_OR_EQUAL("greatThanOrEqual", "大于等于"),
    LESS_THAN_OR_EQUAL("lessThanOrEqual", "小于等于");

    private String code;
    private String desc;

    IndexOperatorType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
