package com.vey.ruleengine.model;

import java.util.Date;
import java.util.Objects;

/**
 * @Auther vey
 * @Date 2018/11/5
 */
public class ExecuteRule {
    /**
     * 规则标识，唯一
     */
    private String code;

    /**
     * 规则内容，通过ExecuteRuleBuilder构建规则JSON字符串
     */
    private String rule;

    /**
     * 规则有效期开始时间，没有则立即生效
     */
    private Date effectStartTime;

    /**
     * 规则有效期结束时间
     */
    private Date effectEndTime;

    /**
     * 是否立即生效
     */
    private boolean immediateEffect;

    /**
     * 每一条规则包含的规则项数
     */
    private Integer conjunctionSize;

    public ExecuteRule() {
    }

    public static ExecuteRule build(String code, String rule, Date effectEndTime) {
        ExecuteRule executeRule = new ExecuteRule();
        executeRule.code = code;
        executeRule.rule = rule;
        executeRule.effectEndTime = effectEndTime;
        executeRule.immediateEffect = true;
        return executeRule;
    }

    public static ExecuteRule build(String code, String rule, Date effectStartTime, Date effectEndTime) {
        ExecuteRule executeRule = new ExecuteRule();
        executeRule.code = code;
        executeRule.rule = rule;
        executeRule.effectStartTime = effectStartTime;
        executeRule.effectEndTime = effectEndTime;
        executeRule.immediateEffect = false;
        return executeRule;
    }

    public String getCode() {
        return code;
    }

    public String getRule() {
        return rule;
    }

    public Date getEffectStartTime() {
        return effectStartTime;
    }

    public Date getEffectEndTime() {
        return effectEndTime;
    }

    public boolean isImmediateEffect() {
        return immediateEffect;
    }

    public Integer getConjunctionSize() {
        return conjunctionSize;
    }

    public void setConjunctionSize(Integer conjunctionSize) {
        this.conjunctionSize = conjunctionSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExecuteRule that = (ExecuteRule) o;
        return immediateEffect == that.immediateEffect &&
                conjunctionSize == that.conjunctionSize &&
                Objects.equals(code, that.code) &&
                Objects.equals(rule, that.rule) &&
                Objects.equals(effectStartTime, that.effectStartTime) &&
                Objects.equals(effectEndTime, that.effectEndTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, rule, effectStartTime, effectEndTime, immediateEffect, conjunctionSize);
    }

    @Override
    public String toString() {
        return "ExecuteRule{" +
                "code=" + code +
                ", rule='" + rule + '\'' +
                ", effectStartTime=" + effectStartTime +
                ", effectEndTime=" + effectEndTime +
                ", immediateEffect=" + immediateEffect +
                ", conjunctionSize=" + conjunctionSize +
                '}';
    }

}
