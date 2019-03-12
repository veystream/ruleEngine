package com.vey.ruleengine.execption;

/**
 * @Auther vey
 * @Date 2018/12/5
 */
public class RuleEngineInternalException extends RuntimeException {

    public RuleEngineInternalException(String s) {
        super(s);
    }

    public RuleEngineInternalException(String s, Throwable t) {
        super(s, t);
    }
}
