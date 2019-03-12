package com.vey.ruleengine.algorithm;

import com.vey.ruleengine.enums.AssignmentOperatorType;
import com.vey.ruleengine.model.ExecuteRule;

import java.util.HashSet;

/**
 * @Auther vey
 * @Date 2018/11/6
 */
public abstract class AbstractIndex {

    /**
     * 从索引中添加规则
     *
     * @param assignmentKey
     * @param assignmentValue
     * @param executeRule
     */
    public abstract void add(String assignmentKey, Object assignmentValue, ExecuteRule executeRule);

    /**
     * 从索引中删除规则
     *
     * @param assignmentKey
     * @param assignmentValue
     * @param executeRule
     */
    public abstract void remove(String assignmentKey, Object assignmentValue, ExecuteRule executeRule);

    /**
     * 匹配规则
     *
     * @param assignmentKey
     * @param assignmentValue
     * @param operateType
     * @return
     */
    public abstract HashSet<String> match(String assignmentKey, Object assignmentValue, AssignmentOperatorType operateType);
}
