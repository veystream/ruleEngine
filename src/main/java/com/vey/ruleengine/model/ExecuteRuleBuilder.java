package com.vey.ruleengine.model;

import com.alibaba.fastjson.JSON;
import com.vey.ruleengine.enums.AssignmentOperatorType;
import com.vey.ruleengine.enums.AssignmentType;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 执行规则
 *
 * @author vey
 */
public class ExecuteRuleBuilder {

    /**
     * 规则转换日期格式化
     */
    private final static String RULE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 规则容器
     */
    private Map<String, Expression> ruleContainer = new ConcurrentHashMap<>();

    /**
     * 新增规则项
     *
     * @param name
     * @param operatorType
     * @param values
     */
    public void addRuleExpression(String name, AssignmentOperatorType operatorType, Object... values) {
        if (name == null || name.length() == 0) {
            throw new RuntimeException("name cannot be empty!");
        }
        if (operatorType == null) {
            throw new RuntimeException("operatorType cannot be null!");
        }
        checkValues(values);
        checkTypeAndValues(operatorType, values);

        ruleContainer.put(name, new Expression(getAssignmentType(values[0]).getCode(), operatorType.getCode(), values));
    }

    /**
     * 获取可执行规则的JSON形式
     *
     * @return
     */
    public String buildToJSONString() {
        return JSON.toJSONStringWithDateFormat(ruleContainer, RULE_DATE_FORMAT);
    }

    /**
     * 检查values
     *
     * @param values
     */
    private void checkValues(Object... values) {
        if (values.length == 0) {
            throw new RuntimeException("values cannot be empty!");
        }
        //检查类型， values的类型必须一致
        Class<?> preValueType = null;
        for (Object value : values) {
            if (preValueType != null && preValueType != value.getClass()) {
                throw new RuntimeException("The type of values cannot be different!");
            }
            preValueType = value.getClass();
        }
    }

    /**
     * 检查类型和参数的匹配情况
     *
     * @param operatorType
     * @param values
     */
    private void checkTypeAndValues(AssignmentOperatorType operatorType, Object[] values) {
        //gt只允许有一个参数，且不支持String
        if (operatorType == AssignmentOperatorType.GREAT_THAN) {
            if (values.length != 1) {
                throw new RuntimeException("gt cannot match except one value!");
            }
            AssignmentType assignmentType = getAssignmentType(values[0]);
            if (assignmentType == AssignmentType.STRING) {
                throw new RuntimeException("gt cannot support String!");
            }
        }
        //eq只允许有一个参数
        if (operatorType == AssignmentOperatorType.EQUAL) {
            if (values.length != 1) {
                throw new RuntimeException("eq cannot match except one value!");
            }
        }
        //lt只允许有一个参数，且不支持String
        if (operatorType == AssignmentOperatorType.LESS_THAN) {
            if (values.length != 1) {
                throw new RuntimeException("lt cannot match except one value!");
            }
            AssignmentType assignmentType = getAssignmentType(values[0]);
            if (assignmentType == AssignmentType.STRING) {
                throw new RuntimeException("lt cannot support String!");
            }
        }
        //bt只允许有一个参数，且不支持String
        if (operatorType == AssignmentOperatorType.BETWEEN) {
            if (values.length != 2) {
                throw new RuntimeException("bt cannot match except two values!");
            }
            AssignmentType assignmentType = getAssignmentType(values[0]);
            if (assignmentType == AssignmentType.STRING) {
                throw new RuntimeException("bt cannot support String!");
            }
            checkBetweenValues(values[0], values[1]);
        }
    }

    /**
     * 检查bt操作的值
     *
     * @param fromValue
     * @param toValue
     */
    private void checkBetweenValues(Object fromValue, Object toValue) {
        if (fromValue instanceof Date && ((Date) fromValue).compareTo((Date) toValue) > 0) {
            throw new RuntimeException("The first value cannot be grater than the second value in bt operator!");
        }
        if (fromValue instanceof Integer && ((Integer) fromValue).compareTo((Integer) toValue) > 0) {
            throw new RuntimeException("The first value cannot be grater than the second value in bt operator!");
        }
        if (fromValue instanceof Long && ((Long) fromValue).compareTo((Long) toValue) > 0) {
            throw new RuntimeException("The first value cannot be grater than the second value in bt operator!");
        }
    }

    /**
     * 根据value获取AssignmentType
     *
     * @param value
     * @return
     */
    private AssignmentType getAssignmentType(Object value) {
        if (value instanceof Date) {
            return AssignmentType.DATE;
        }
        if (value instanceof Integer) {
            return AssignmentType.INTEGER;
        }
        if (value instanceof Long) {
            return AssignmentType.LONG;
        }
        if (value instanceof String) {
            return AssignmentType.STRING;
        }
        throw new RuntimeException("Cannot support type:" + value.getClass());
    }
}
