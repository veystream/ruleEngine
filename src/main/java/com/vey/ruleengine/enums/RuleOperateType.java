package com.vey.ruleengine.enums;

/**
 * @Auther vey
 * @Date 2018/11/7
 */
public enum RuleOperateType {
    ADD("add", "新增"),
    REMOVE("remove", "删除");

    private String code;
    private String desc;

    RuleOperateType(String code, String desc) {
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
