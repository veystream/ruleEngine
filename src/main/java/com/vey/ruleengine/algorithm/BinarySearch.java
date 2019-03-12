package com.vey.ruleengine.algorithm;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Auther vey
 * @Date 2018/11/7
 */
class BinarySearch {

    /**
     * 查找数组中小于条件的规则
     *
     * @param array
     * @param condition
     * @param allowEqual
     * @return
     */
    public static HashSet<String> conditionGreatThan(Object[] array, Object condition, boolean allowEqual,
                                                   Map<Object, CopyOnWriteArraySet<String>> rangeDict) {
        int from = 0;
        int to = array.length - 1;

        while (from <= to) {
            int middle = (from + to) / 2;
            if (condition instanceof Long) {
                if (allowEqual) {
                    if ((long) array[middle] > (long) condition) {
                        to = middle - 1;
                    } else {
                        from = middle + 1;
                    }
                } else {
                    if ((long) array[middle] >= (long) condition) {
                        to = middle - 1;
                    } else {
                        from = middle + 1;
                    }
                }
            } else if (condition instanceof Integer) {
                if (allowEqual) {
                    if ((int) array[middle] > (int) condition) {
                        to = middle - 1;
                    } else {
                        from = middle + 1;
                    }
                } else {
                    if ((int) array[middle] >= (int) condition) {
                        to = middle - 1;
                    } else {
                        from = middle + 1;
                    }
                }
            } else {
                return null;
            }
        }

        HashSet<String> ruleCodes = new HashSet<>(to + 1);
        for (int i = 0; i <= to; i++) {
            for (String ruleCode : rangeDict.get(array[i])) {
                ruleCodes.add(ruleCode);
            }
        }

        return ruleCodes;
    }

    /**
     * 查找数组中大于条件的规则
     *
     * @param ruleArray
     * @param condition
     * @param allowEqual
     * @return
     */
    public static HashSet<String> conditionLessThan(Object[] ruleArray, Object condition, boolean allowEqual, Map<Object, CopyOnWriteArraySet<String>> rangeDict) {
        int from = 0;
        int to = ruleArray.length - 1;

        while (from <= to) {
            int middle = (from + to) / 2;
            if (condition instanceof Long) {
                if (allowEqual) {
                    if ((long) ruleArray[middle] >= (long) condition) {
                        to = middle - 1;
                    } else {
                        from = middle + 1;
                    }
                } else {
                    if ((long) ruleArray[middle] > (long) condition) {
                        to = middle - 1;
                    } else {
                        from = middle + 1;
                    }
                }
            } else if (condition instanceof Integer) {
                if (allowEqual) {
                    if ((int) ruleArray[middle] >= (int) condition) {
                        to = middle - 1;
                    } else {
                        from = middle + 1;
                    }
                } else {
                    if ((int) ruleArray[middle] > (int) condition) {
                        to = middle - 1;
                    } else {
                        from = middle + 1;
                    }
                }
            } else {
                return null;
            }
        }

        HashSet<String> ruleCodes = new HashSet<>(ruleArray.length);
        for (int i = (ruleArray.length - 1); i >= from; i--) {
            for (String ruleCode : rangeDict.get(ruleArray[i])) {
                ruleCodes.add(ruleCode);
            }
        }

        return ruleCodes;
    }
}