package com.vey.ruleengine.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vey.ruleengine.enums.AssignmentOperatorType;
import com.vey.ruleengine.enums.AssignmentType;
import com.vey.ruleengine.enums.RuleOperateType;
import com.vey.ruleengine.execption.ErrorMessage;
import com.vey.ruleengine.execption.RuleEngineInternalException;
import com.vey.ruleengine.factory.AlgorithmIndexFactory;
import com.vey.ruleengine.model.AssignmentOperator;
import com.vey.ruleengine.model.ExecuteRule;
import com.vey.ruleengine.model.Expression;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Auther vey
 * @Date 2018/11/6
 */
@Component
public class AssignmentManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssignmentManager.class);

    @Resource
    private AlgorithmIndexFactory AlgorithmIndexFactory;

    /**
     * 所有内存中的规则索引。新增规则需要同步新增，规则失效及删除规则同步删除
     */
    private final ConcurrentHashMap<String, ExecuteRule> executeRuleMaps = new ConcurrentHashMap<>();

    /**
     * 规则对应的运算方式存储到内存，load和add规则都需要维护
     * key：规则的key
     * value：操作方式和操作符号
     */
    private ConcurrentHashMap<String, CopyOnWriteArraySet<AssignmentOperator>> assignmentOperators = new ConcurrentHashMap<>();

    /**
     * 新增规则
     *
     * @param executeRule
     * @return
     */
    public boolean add(ExecuteRule executeRule) {
        LOGGER.info("[AssignmentManager.add] add rule start! executeRule: {}, ruleSize: {}", executeRule, executeRuleMaps.size());
        if (executeRuleMaps.containsKey(executeRule.getCode())) {
            LOGGER.warn("[AssignmentManager.add] executeRule is exists! executeRule: {}", executeRule);
            return false;
        }
        String rule = executeRule.getRule();
        JSONObject conjunction = (JSONObject) JSONObject.parse(rule);
        if (conjunction == null) {
            LOGGER.warn("[AssignmentManager.add] add rule failed! conjunction is null! executeRule: {}", executeRule);
            throw new IllegalArgumentException(ErrorMessage.ILLEGAL_ARGUMENTS.getMessage());
        }
        if (conjunction.size() == 0) {
            LOGGER.warn("[AssignmentManager.add] add rule failed! conjunction is empty! executeRule: {}", executeRule);
            return false;
        }

        try {
            int conjunctionSize = 0;
            for (String assignmentKey : conjunction.keySet()) {
                JSONObject jsonObject = (JSONObject) conjunction.get(assignmentKey);
                Expression expression = JSON.parseObject(jsonObject.toJSONString(), Expression.class);

                if (!putToAssignmentOperators(assignmentKey, expression)) {
                    LOGGER.warn("[AssignmentManager.add] init execute rule failed! putToAssignmentOperators failed! executeRule: {}", executeRule);
                    throw new IllegalArgumentException(ErrorMessage.ILLEGAL_ARGUMENTS.getMessage());
                }
                AlgorithmIndexFactory.operate(executeRule, assignmentKey, expression, RuleOperateType.ADD);

                AssignmentOperatorType assignmentOperatorType = AssignmentOperatorType.of(expression.getOperator());
                conjunctionSize = conjunctionSize + assignmentOperatorType.getSize();
            }
            executeRule.setConjunctionSize(conjunctionSize);
            putToExecuteRuleMap(executeRule.getCode(), executeRule);
        } catch (Exception e) {
            LOGGER.error("[AssignmentManager.add] add rule exception! executeRule: {}, ruleSize: {}", executeRule, executeRuleMaps.size(), e);
            throw new RuleEngineInternalException(ErrorMessage.INTERNAL_ERROR.getMessage(), e);
        }

        LOGGER.info("[AssignmentManager.add] add rule success! executeRule: {}, ruleSize: {}", executeRule, executeRuleMaps.size());
        return true;
    }

    /**
     * 规则匹配
     *
     * @param condition
     * @return
     */
    public List<ExecuteRule> match(String condition) {
        LOGGER.info("[AssignmentManager.match] match rule start! condition: {}, ruleSize: {}", condition, executeRuleMaps.size());
        List<ExecuteRule> executeRules = new ArrayList<>();
        JSONObject conditionJson = (JSONObject) JSONObject.parse(condition);
        if (CollectionUtils.isEmpty(assignmentOperators)) {
            LOGGER.warn("[AssignmentManager.match] match rule failed! assignmentOperators is null!");
            return executeRules;
        }

        if (conditionJson == null || conditionJson.size() == 0) {
            LOGGER.warn("[AssignmentManager.match] match rule failed! condition is null! condition: {}", condition);
            return executeRules;
        }

        try {
            HashMap<String, Integer> ruleMatchedCount = new HashMap<>();
            for (String assignmentKey : conditionJson.keySet()) {
                Object assignmentValue = conditionJson.get(assignmentKey);
                CopyOnWriteArraySet<AssignmentOperator> assignmentOperatorSet = assignmentOperators.get(assignmentKey);
                if (assignmentOperators == null || assignmentOperatorSet.size() == 0) {
                    continue;
                }
                List<String> ruleCodes = AlgorithmIndexFactory.ruleMatchedCount(assignmentKey, assignmentValue, assignmentOperatorSet);
                for (String ruleCode : ruleCodes) {
                    putRuleMatchedCount(ruleMatchedCount, ruleCode);
                }
            }
            for (String ruleCode : ruleMatchedCount.keySet()) {
                ExecuteRule executeRule = getFromExecuteRuleMap(ruleCode);
                if (executeRule == null) {
                    LOGGER.warn("[AssignmentManager.match] matched executeRule is null, ruleMatchedCount: {}, ruleCode: {}", ruleMatchedCount.get(ruleCode), ruleCode);
                    continue;
                }
                if (executeRule.getConjunctionSize().equals(ruleMatchedCount.get(ruleCode))) {
                    executeRules.add(executeRule);
                }
            }
        } catch (Exception e) {
            LOGGER.error("[AssignmentManager.match] match rule exception! condition: {}, ruleSize: {}", condition, executeRuleMaps.size(), e);
            throw new RuleEngineInternalException(ErrorMessage.INTERNAL_ERROR.getMessage(), e);
        }

        LOGGER.info("[AssignmentManager.match] match rule success! condition: {}, ruleSize: {}", condition, executeRuleMaps.size());
        return executeRules;
    }

    /**
     * 移除规则
     *
     * @param ruleCode
     * @return
     */
    public boolean remove(String ruleCode) {
        LOGGER.info("[AssignmentManager.remove] remove rule start! ruleCode: {}, ruleSize: {}", ruleCode, executeRuleMaps.size());
        ExecuteRule executeRule = getFromExecuteRuleMap(ruleCode);
        if (executeRule == null) {
            return true;
        }
        String rule = executeRule.getRule();
        JSONObject conjunction = (JSONObject) JSONObject.parse(rule);
        if (conjunction == null) {
            LOGGER.warn("[AssignmentManager.remove] remove rule failed! conjunction is null! executeRule: {}", executeRule);
            throw new IllegalArgumentException(ErrorMessage.ILLEGAL_ARGUMENTS.getMessage());
        }

        try {
            for (String assignmentKey : conjunction.keySet()) {
                JSONObject jsonObject = (JSONObject) conjunction.get(assignmentKey);
                Expression expression = JSON.parseObject(jsonObject.toJSONString(), Expression.class);
                AlgorithmIndexFactory.operate(executeRule, assignmentKey, expression, RuleOperateType.REMOVE);
            }
            executeRuleMaps.remove(executeRule.getCode());
        } catch (Exception e) {
            LOGGER.error("[AssignmentManager.remove] remove rule exception! executeRule: {}, ruleSize: {}", executeRule, executeRuleMaps.size(), e);
            throw new RuleEngineInternalException(ErrorMessage.INTERNAL_ERROR.getMessage(), e);
        }

        LOGGER.info("[AssignmentManager.remove] remove rule success! executeRule: {}, ruleSize: {}", executeRule, executeRuleMaps.size());
        return true;
    }

    /**
     * 检查运算方式是否符合规范
     *
     * @param assignmentKey
     * @param expression
     * @return
     */
    private boolean putToAssignmentOperators(String assignmentKey, Expression expression) {
        if (StringUtils.isBlank(expression.getOperator()) || StringUtils.isBlank(expression.getType())) {
            return false;
        }
        AssignmentOperator assignmentOperator = new AssignmentOperator(AssignmentOperatorType.of(expression.getOperator()), AssignmentType.of(expression.getType()));
        CopyOnWriteArraySet<AssignmentOperator> assignmentOperatorSet = new CopyOnWriteArraySet<>();
        if (assignmentOperators.containsKey(assignmentKey)) {
            assignmentOperatorSet = assignmentOperators.get(assignmentKey);
        }
        assignmentOperatorSet.add(assignmentOperator);
        assignmentOperators.put(assignmentKey, assignmentOperatorSet);
        return true;
    }


    /**
     * 命中的规则次数统计
     *
     * @param ruleMatchedCount
     * @param ruleCode
     */
    private void putRuleMatchedCount(HashMap<String, Integer> ruleMatchedCount, String ruleCode) {
        if (ruleMatchedCount.containsKey(ruleCode)) {
            int count = ruleMatchedCount.get(ruleCode);
            ruleMatchedCount.put(ruleCode, count + 1);
        } else {
            ruleMatchedCount.put(ruleCode, 1);
        }
    }

    private void putToExecuteRuleMap(String code, ExecuteRule executeRule) {
        executeRuleMaps.put(code, executeRule);
    }

    private ExecuteRule getFromExecuteRuleMap(String ruleCode) {
        return executeRuleMaps.get(ruleCode);
    }
}
