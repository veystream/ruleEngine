package com.vey.ruleengine.factory;

import com.vey.ruleengine.algorithm.AbstractIndex;
import com.vey.ruleengine.algorithm.InvertedIndex;
import com.vey.ruleengine.algorithm.RangeIndex;
import com.vey.ruleengine.contansts.IndexContants;
import com.vey.ruleengine.enums.AssignmentOperatorType;
import com.vey.ruleengine.enums.AssignmentType;
import com.vey.ruleengine.enums.RuleOperateType;
import com.vey.ruleengine.execption.ErrorMessage;
import com.vey.ruleengine.model.AssignmentOperator;
import com.vey.ruleengine.model.ExecuteRule;
import com.vey.ruleengine.model.Expression;
import com.vey.ruleengine.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @Auther vey
 * @Date 2018/11/5
 */
@Component
public class AlgorithmIndexFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlgorithmIndexFactory.class);

    @Resource
    private InvertedIndex invertedIndex;

    @Resource
    private RangeIndex rangeIndex;

    public void operate(ExecuteRule executeRule, String assignmentKey, Expression expression, RuleOperateType ruleOperateType) {
        String type = expression.getType();
        String operator = expression.getOperator();
        Object[] operandArr = expression.getOperand();

        AssignmentOperatorType assignmentOperatorType = AssignmentOperatorType.of(operator);
        AbstractIndex abstractIndex = getIndexAlgorithm(assignmentOperatorType);
        if (abstractIndex == null) {
            LOGGER.warn("[AlgorithmIndexFactory.operate] operate assignment failed! abstractIndex is null! assignmentKey: {}, expression: {}", assignmentKey, expression);
            throw new IllegalArgumentException(ErrorMessage.ILLEGAL_ARGUMENTS.getMessage());
        }

        if (AssignmentOperatorType.BETWEEN.equals(assignmentOperatorType)) {
            if (operandArr.length != 2) {
                LOGGER.warn("[AlgorithmIndexFactory.operate] operate assignment failed! operand is invalid! assignmentKey: {}, expression: {}", assignmentKey, expression);
                throw new IllegalArgumentException(ErrorMessage.ILLEGAL_ARGUMENTS.getMessage());
            }
            Object from = translateAssignmentValue(operandArr[0], type);
            Object to = translateAssignmentValue(operandArr[1], type);
            operate(ruleOperateType, abstractIndex, getBetweenFromAssignmentKey(assignmentKey), from, executeRule);
            operate(ruleOperateType, abstractIndex, getBetweenToAssignmentKey(assignmentKey), to, executeRule);
        } else if (AssignmentOperatorType.GREAT_THAN.equals(assignmentOperatorType)) {
            if (operandArr.length != 1) {
                LOGGER.warn("[AlgorithmIndexFactory.operate] operate assignment failed! operand is invalid! assignmentKey: {}, expression: {}", assignmentKey, expression);
                throw new IllegalArgumentException(ErrorMessage.ILLEGAL_ARGUMENTS.getMessage());
            }
            Object from = translateAssignmentValue(operandArr[0], type);
            operate(ruleOperateType, abstractIndex, getFromAssignmentKey(assignmentKey), from, executeRule);
        } else if (AssignmentOperatorType.LESS_THAN.equals(assignmentOperatorType)) {
            if (operandArr.length != 1) {
                LOGGER.warn("[AlgorithmIndexFactory.operate] operate assignment failed! operand is invalid! assignmentKey: {}, expression: {}", assignmentKey, expression);
                throw new IllegalArgumentException(ErrorMessage.ILLEGAL_ARGUMENTS.getMessage());
            }
            Object to = translateAssignmentValue(operandArr[0], type);
            operate(ruleOperateType, abstractIndex, getToAssignmentKey(assignmentKey), to, executeRule);
        } else if (assignmentOperatorType.IN.equals(assignmentOperatorType)) {
            if (operandArr.length < 1) {
                LOGGER.warn("[AlgorithmIndexFactory.operate] operate assignment failed! operand is invalid! assignmentKey: {}, expression: {}", assignmentKey, expression);
                throw new IllegalArgumentException(ErrorMessage.ILLEGAL_ARGUMENTS.getMessage());
            }
            for (Object operand : operandArr) {
                operate(ruleOperateType, abstractIndex, assignmentKey, operand, executeRule);
            }
        } else if (assignmentOperatorType.EQUAL.equals(assignmentOperatorType)) {
            if (operandArr.length != 1) {
                LOGGER.warn("[AlgorithmIndexFactory.operate] operate assignment failed! operand is invalid! assignmentKey: {}, expression: {}", assignmentKey, expression);
                throw new IllegalArgumentException(ErrorMessage.ILLEGAL_ARGUMENTS.getMessage());
            }
            operate(ruleOperateType, abstractIndex, assignmentKey, operandArr[0], executeRule);
        } else {
            LOGGER.warn("[AlgorithmIndexFactory.operate] operate assignment failed! operator is invalid! assignmentKey: {}, expression: {}", assignmentKey, expression);
            throw new IllegalArgumentException(ErrorMessage.ILLEGAL_ARGUMENTS.getMessage());
        }
    }

    public List<String> ruleMatchedCount(String assignmentKey, Object assignmentValue, AssignmentOperator assignmentOperator) {
        List<String> ruleCodes = new ArrayList<>();
        AssignmentOperatorType assignmentOperatorType = assignmentOperator.getOperatorType();
        AssignmentType type = assignmentOperator.getType();
        if (assignmentOperatorType == null || type == null) {
            LOGGER.warn("[AlgorithmIndexFactory.match] match rule failed! operatorType or type not exists! assignmentKey: {}, assignmentValue: {}, assignmentOperatorSet: {}", assignmentKey, assignmentValue, assignmentOperator);
            throw new RuntimeException(ErrorMessage.ASSIGNMENT_OPERATOR_INVALID.getMessage());
        }

        AbstractIndex abstractIndex = getIndexAlgorithm(assignmentOperatorType);
        if (abstractIndex == null) {
            LOGGER.warn("[AlgorithmIndexFactory.match] match assignment failed! abstractIndex is null! assignmentKey: {}, assignmentValue: {}, assignmentOperatorSet: {}", assignmentKey, assignmentValue, assignmentOperator);
            throw new IllegalArgumentException(ErrorMessage.ILLEGAL_ARGUMENTS.getMessage());
        }
        if (AssignmentOperatorType.BETWEEN.equals(assignmentOperatorType)) {
            HashSet<String> ruleCodeFrom = abstractIndex.match(getBetweenFromAssignmentKey(assignmentKey), translateAssignmentValue(assignmentValue, type.getCode()), AssignmentOperatorType.GREAT_THAN);
            HashSet<String> ruleCodeTo = abstractIndex.match(getBetweenToAssignmentKey(assignmentKey), translateAssignmentValue(assignmentValue, type.getCode()), AssignmentOperatorType.LESS_THAN);
            ruleCodes.addAll(ruleCodeFrom);
            ruleCodes.addAll(ruleCodeTo);
        } else if (AssignmentOperatorType.GREAT_THAN.equals(assignmentOperatorType)) {
            HashSet<String> matchRuleCodes = abstractIndex.match(getFromAssignmentKey(assignmentKey), translateAssignmentValue(assignmentValue, type.getCode()), AssignmentOperatorType.GREAT_THAN);
            if (!CollectionUtils.isEmpty(matchRuleCodes)) {
                ruleCodes.addAll(matchRuleCodes);
            }
        } else if (AssignmentOperatorType.LESS_THAN.equals(assignmentOperatorType)) {
            HashSet<String> matchRuleCodes = abstractIndex.match(getToAssignmentKey(assignmentKey), translateAssignmentValue(assignmentValue, type.getCode()), AssignmentOperatorType.LESS_THAN);
            if (!CollectionUtils.isEmpty(matchRuleCodes)) {
                ruleCodes.addAll(matchRuleCodes);
            }
        } else if (assignmentOperatorType.IN.equals(assignmentOperatorType)) {
            HashSet<String> matchRuleCodes = abstractIndex.match(assignmentKey, translateAssignmentValue(assignmentValue, type.getCode()), AssignmentOperatorType.EQUAL);
            if (!CollectionUtils.isEmpty(matchRuleCodes)) {
                ruleCodes.addAll(matchRuleCodes);
            }
        } else if (assignmentOperatorType.EQUAL.equals(assignmentOperatorType)) {
            HashSet<String> matchRuleCodes = abstractIndex.match(assignmentKey, translateAssignmentValue(assignmentValue, type.getCode()), AssignmentOperatorType.EQUAL);
            if (!CollectionUtils.isEmpty(matchRuleCodes)) {
                ruleCodes.addAll(matchRuleCodes);
            }
        }
        return ruleCodes;
    }

    /**
     * 根据操作类型，获取索引算法
     *
     * @param assignmentOperatorType
     * @return
     */
    private AbstractIndex getIndexAlgorithm(AssignmentOperatorType assignmentOperatorType) {
        if (AssignmentOperatorType.EQUAL.equals(assignmentOperatorType) || AssignmentOperatorType.IN.equals(assignmentOperatorType)) {
            return invertedIndex;
        } else if (AssignmentOperatorType.BETWEEN.equals(assignmentOperatorType)
                || AssignmentOperatorType.GREAT_THAN.equals(assignmentOperatorType)
                || AssignmentOperatorType.LESS_THAN.equals(assignmentOperatorType)
        ) {
            return rangeIndex;
        }
        return null;
    }

    /**
     * 范围索引assignmentKey做转换，用于在索引中区别开始范围的索引还是结束范围的索引
     *
     * @param assignmentKey
     * @return
     */
    private String getFromAssignmentKey(String assignmentKey) {
        StringBuilder stringBuilder = new StringBuilder(assignmentKey).append(IndexContants.FROM_KEY_PREFIX);
        return stringBuilder.toString();
    }

    /**
     * 范围索引assignmentKey做转换，用于在索引中区别开始范围的索引还是结束范围的索引
     *
     * @param assignmentKey
     * @return
     */
    private String getToAssignmentKey(String assignmentKey) {
        StringBuilder stringBuilder = new StringBuilder(assignmentKey).append(IndexContants.TO_KEY_PREFIX);
        return stringBuilder.toString();
    }

    /**
     * 范围索引assignmentKey做转换，用于在索引中区别开始范围的索引还是结束范围的索引
     *
     * @param assignmentKey
     * @return
     */
    private String getBetweenFromAssignmentKey(String assignmentKey) {
        StringBuilder stringBuilder = new StringBuilder(assignmentKey).append(IndexContants.BETWEEN_FROM_KEY_PREFIX);
        return stringBuilder.toString();
    }

    /**
     * 范围索引assignmentKey做转换，用于在索引中区别开始范围的索引还是结束范围的索引
     *
     * @param assignmentKey
     * @return
     */
    private String getBetweenToAssignmentKey(String assignmentKey) {
        StringBuilder stringBuilder = new StringBuilder(assignmentKey).append(IndexContants.BETWEEN_TO_KEY_PREFIX);
        return stringBuilder.toString();
    }

    private void operate(RuleOperateType ruleOperateType, AbstractIndex abstractIndex, String assignmentKey, Object assignmentValue, ExecuteRule executeRule) {
        if (RuleOperateType.ADD.equals(ruleOperateType)) {
            abstractIndex.add(assignmentKey, assignmentValue, executeRule);
        } else if (RuleOperateType.REMOVE.equals(ruleOperateType)) {
            abstractIndex.remove(assignmentKey, assignmentValue, executeRule);
        }
    }

    private Object translateAssignmentValue(Object assignmentValue, String type) {
        if (AssignmentType.DATE.getCode().equals(type)) {
            assignmentValue = DateUtils.translate(DateUtils.format((String) assignmentValue));
        }
        return assignmentValue;
    }
}
