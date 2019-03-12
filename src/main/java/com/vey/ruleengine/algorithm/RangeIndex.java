package com.vey.ruleengine.algorithm;

import com.vey.ruleengine.enums.IndexOperatorType;
import com.vey.ruleengine.model.ExecuteRule;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Auther vey
 * @Date 2018/11/5
 */
@Component
public class RangeIndex extends AbstractIndex {
    private ConcurrentHashMap<String, Map<Object, CopyOnWriteArraySet<String>>> DICT = new ConcurrentHashMap<>();

    @Override
    public void add(String assignmentKey, Object assignmentValue, ExecuteRule executeRule) {
        if (DICT.containsKey(assignmentKey)) {
            Map<Object, CopyOnWriteArraySet<String>> rangeDict = DICT.get(assignmentKey);
            if (rangeDict.containsKey(assignmentValue)) {
                CopyOnWriteArraySet<String> ruleCodes = rangeDict.get(assignmentValue);
                if (!ruleCodes.contains(executeRule.getCode())) {
                    ruleCodes.add(executeRule.getCode());
                    rangeDict.put(assignmentValue, ruleCodes);
                    DICT.put(assignmentKey, rangeDict);
                }
            } else {
                CopyOnWriteArraySet<String> ruleCodes = new CopyOnWriteArraySet<>();
                ruleCodes.add(executeRule.getCode());
                rangeDict.put(assignmentValue, ruleCodes);
                DICT.put(assignmentKey, rangeDict);
            }
        } else {
            Map<Object, CopyOnWriteArraySet<String>> rangeDict = Collections.synchronizedMap(new TreeMap<>());
            CopyOnWriteArraySet<String> ruleCodes = new CopyOnWriteArraySet<>();
            ruleCodes.add(executeRule.getCode());
            rangeDict.put(assignmentValue, ruleCodes);
            DICT.put(assignmentKey, rangeDict);
        }
    }

    @Override
    public void remove(String assignmentKey, Object assignmentValue, ExecuteRule executeRule) {
        if (DICT.containsKey(assignmentKey)) {
            Map<Object, CopyOnWriteArraySet<String>> rangeDict = DICT.get(assignmentKey);
            if (rangeDict.containsKey(assignmentValue)) {
                CopyOnWriteArraySet<String> ruleCodes = rangeDict.get(assignmentValue);
                ruleCodes.remove(executeRule.getCode());
                if (ruleCodes.size() == 0) {
                    rangeDict.remove(assignmentValue);
                }
                if (rangeDict.size() == 0) {
                    DICT.remove(assignmentKey);
                }
            }
        }
    }

    @Override
    public HashSet<String> match(String assignmentKey, Object assignmentValue, IndexOperatorType operateType) {
        if (DICT.containsKey(assignmentKey)) {
            Map<Object, CopyOnWriteArraySet<String>> rangeDict = DICT.get(assignmentKey);

            HashSet<String> ruleCodes = new HashSet<>();
            Object[] rangeValues = rangeDict.keySet().toArray(new Object[rangeDict.size()]);
            if (IndexOperatorType.GREAT_THAN.equals(operateType)) {
                ruleCodes = BinarySearch.conditionGreatThan(rangeValues, assignmentValue, false, rangeDict);
            } else if (IndexOperatorType.LESS_THAN.equals(operateType)) {
                ruleCodes = BinarySearch.conditionLessThan(rangeValues, assignmentValue, false, rangeDict);
            } else if (IndexOperatorType.GREAT_THAN_OR_EQUAL.equals(operateType)) {
                ruleCodes = BinarySearch.conditionGreatThan(rangeValues, assignmentValue, true, rangeDict);
            } else if (IndexOperatorType.LESS_THAN_OR_EQUAL.equals(operateType)) {
                ruleCodes = BinarySearch.conditionLessThan(rangeValues, assignmentValue, true, rangeDict);
            }
            return ruleCodes;
        }
        return new HashSet<>();
    }

}
