package com.vey.ruleengine.model;

import com.vey.ruleengine.enums.AssignmentOperatorType;
import com.vey.ruleengine.enums.AssignmentType;

import java.util.Objects;

/**
 * @Auther vey
 * @Date 2018/11/8
 */
public class AssignmentOperator {
    private AssignmentOperatorType operatorType;
    private AssignmentType type;

    public AssignmentOperator(AssignmentOperatorType operatorType, AssignmentType type) {
        this.operatorType = operatorType;
        this.type = type;
    }

    public AssignmentOperatorType getOperatorType() {
        return operatorType;
    }

    public AssignmentType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssignmentOperator that = (AssignmentOperator) o;
        return operatorType == that.operatorType &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(operatorType, type);
    }
}
