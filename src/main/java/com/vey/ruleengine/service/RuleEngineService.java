package com.vey.ruleengine.service;

import com.vey.ruleengine.execption.ErrorMessage;
import com.vey.ruleengine.manager.AssignmentManager;
import com.vey.ruleengine.manager.EffectTimerManager;
import com.vey.ruleengine.manager.InvalidTimerManager;
import com.vey.ruleengine.model.ExecuteRule;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Auther vey
 * @Date 2018/11/5
 */
public class RuleEngineService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleEngineService.class);

    @Resource
    private AssignmentManager assignmentManager;
    @Resource
    private EffectTimerManager effectTimerManager;
    @Resource
    private InvalidTimerManager invalidTimerManager;

    /**
     * 新增规则
     *
     * @param executeRule
     * @return
     */
    public boolean add(ExecuteRule executeRule) {
        LOGGER.info("[RuleEngineService.add] add execute rule start! executeRule: {}", executeRule);
        long start = System.currentTimeMillis();
        if (isValidRule(executeRule)) {
            LOGGER.warn("[RuleEngineService.add] execute rule is invalid! executeRule: {}", executeRule);
            throw new IllegalArgumentException(ErrorMessage.ILLEGAL_ARGUMENTS.getMessage());
        }
        // 过期不载入，生效中的载入，未生效的加入定时任务
        if (!isEffectTime(executeRule.getEffectStartTime(), executeRule.getEffectEndTime(), executeRule.isImmediateEffect())) {
            LOGGER.warn("[RuleEngineService.add] execute rule is overdue! executeRule: {}", executeRule);
            throw new IllegalArgumentException(ErrorMessage.ILLEGAL_ARGUMENTS.getMessage());
        }
        // 等到生效日期再生效
        if (isSoonEffectTime(executeRule.getEffectStartTime(), executeRule.isImmediateEffect())) {
            LOGGER.warn("[RuleEngineService.add] execute rule not effect! executeRule: {}", executeRule);
            effectTimerManager.add(executeRule);
            return true;
        }
        boolean result = assignmentManager.add(executeRule);
        long timeConsuming = System.currentTimeMillis() - start;
        if (result) {
            // 规则到期后失效
            invalidTimerManager.add(executeRule);
            LOGGER.info("[RuleEngineService.add] add execute rule success! executeRule: {}, timeConsuming: {}", executeRule, timeConsuming);
        }
        return true;
    }

    /**
     * 匹配规则
     *
     * @param condition
     * @return
     */
    public List<ExecuteRule> match(String condition) {
        LOGGER.info("[RuleEngineService.match] match condition start! condition: {}", condition);
        long start = System.currentTimeMillis();
        if (StringUtils.isBlank(condition)) {
            LOGGER.warn("[RuleEngineService.match] condition is invalid! condition: {}", condition);
            throw new IllegalArgumentException(ErrorMessage.ILLEGAL_ARGUMENTS.getMessage());
        }
        List<ExecuteRule> rules = assignmentManager.match(condition);
        long timeConsuming = System.currentTimeMillis() - start;
        LOGGER.info("[RuleEngineService.match] match condition success! condition: {}, rules: {}, timeConsuming: {}", condition, rules, timeConsuming);
        return rules;
    }

    /**
     * 检查规则是否有效
     *
     * @param executeRule
     * @return
     */
    private boolean isValidRule(ExecuteRule executeRule) {
        return executeRule == null || executeRule.getCode() == null || executeRule.getRule() == null;
    }

    /**
     * 是否有效时间
     *
     * @param startTime
     * @param endTime
     * @return
     */
    private boolean isEffectTime(Date startTime, Date endTime, boolean immediateEffect) {
        Date now = new Date();
        if (immediateEffect) {
            return now.before(endTime);
        }
        return now.after(startTime) && now.before(endTime);
    }

    /**
     * 是否即将生效
     *
     * @param startTime
     * @return
     */
    private boolean isSoonEffectTime(Date startTime, boolean immediateEffect) {
        Date now = new Date();
        if (immediateEffect) {
            return false;
        }
        return now.before(startTime);
    }
}
