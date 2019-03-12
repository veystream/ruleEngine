package com.vey.ruleengine.algorithm;

import com.vey.ruleengine.contansts.IndexContants;
import com.vey.ruleengine.enums.AssignmentOperatorType;
import com.vey.ruleengine.model.ExecuteRule;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Auther vey
 * @Date 2018/11/5
 */
@Component
public class InvertedIndex extends AbstractIndex {
    private ConcurrentHashMap<String, CopyOnWriteArraySet<String>> DICT = new ConcurrentHashMap<>();

    @Override
    public void add(String assignmentKey, Object assignmentValue, ExecuteRule executeRule) {
        String index = getIndex(assignmentKey, assignmentValue);
        if (DICT.containsKey(index)) {
            CopyOnWriteArraySet<String> ruleCodes = DICT.get(index);
            if (!ruleCodes.contains(executeRule.getCode())) {
                ruleCodes.add(executeRule.getCode());
                DICT.put(index, ruleCodes);
            }
        } else {
            CopyOnWriteArraySet<String> ruleCodes = new CopyOnWriteArraySet<>();
            ruleCodes.add(executeRule.getCode());
            DICT.put(index, ruleCodes);
        }
    }

    @Override
    public void remove(String assignmentKey, Object assignmentValue, ExecuteRule executeRule) {
        String index = getIndex(assignmentKey, assignmentValue);
        if (DICT.containsKey(index)) {
            CopyOnWriteArraySet<String> ruleCodes = DICT.get(index);
            ruleCodes.remove(executeRule.getCode());
            if (ruleCodes.size() == 0) {
                DICT.remove(index);
            }
        }
    }

    @Override
    public HashSet<String> match(String assignmentKey, Object assignmentValue, AssignmentOperatorType operateType) {
        String index = getIndex(assignmentKey, assignmentValue);
        if (DICT.containsKey(index)) {
            Set<String> ruleCodes = DICT.get(index);
            return new HashSet<>(ruleCodes);
        }
        return new HashSet<>();
    }

    private String getIndex(String assignmentKey, Object assignmentValue) {
        StringBuilder stringBuilder = new StringBuilder(assignmentKey).append(IndexContants.CONNECTOR).append(assignmentValue);
        return stringBuilder.toString();
    }
}
