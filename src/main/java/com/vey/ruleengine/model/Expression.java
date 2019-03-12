package com.vey.ruleengine.model;

import com.vey.ruleengine.enums.AssignmentOperatorType;
import com.vey.ruleengine.enums.AssignmentType;

/**
 * 规则表达式
 *
 * @author vey
 */
public class Expression {

    /**
     * 数据类型 {@link AssignmentType}
     */
    private String type;

    /**
     * 操作符 {@link AssignmentOperatorType}
     */
    private String operator;

    /**
     * 操作数
     */
    private Object[] operand;

    public Expression(String type, String operator, Object[] operand) {
        this.type = type;
        this.operator = operator;
        this.operand = operand;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Object[] getOperand() {
        return operand;
    }

    public void setOperand(Object[] operand) {
        this.operand = operand;
    }
}
