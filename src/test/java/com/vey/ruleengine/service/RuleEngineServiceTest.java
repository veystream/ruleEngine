package com.vey.ruleengine.service;

import com.vey.ruleengine.AbstractTest;
import com.vey.ruleengine.enums.AssignmentOperatorType;
import com.vey.ruleengine.model.ExecuteRule;
import com.vey.ruleengine.model.ExecuteRuleBuilder;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * @Auther vey
 * @Date 2018/11/6
 */
public class RuleEngineServiceTest extends AbstractTest {

    public static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
    @Autowired
    private RuleEngineService ruleEngineService;

    @Test
    public void testMatch() throws ParseException {
        Date endDate = DateUtils.addDays(new Date(), 5);
        ExecuteRuleBuilder executeRuleBuilder1 = new ExecuteRuleBuilder();
        executeRuleBuilder1.addRuleExpression("gender", AssignmentOperatorType.IN, "male");
        executeRuleBuilder1.addRuleExpression("hobby", AssignmentOperatorType.IN, "travel", "film");
        executeRuleBuilder1.addRuleExpression("validDate", AssignmentOperatorType.BETWEEN, new Date[]{DateUtils.parseDate("2018-01-01 00:00:00", yyyyMMddHHmmss), DateUtils.parseDate("2018-12-31 23:59:59", yyyyMMddHHmmss)});
        // String user1 = "{\"gender\": {\"type\": \"String\",\"operator\": \"in\",\"operand\": [\"male\"]},\"hobby\": {\"type\": \"String\",\"operator\": \"in\",\"operand\": [\"travel\", \"film\"]},\"validDate\": {\"type\": \"Date\",\"operator\": \"bt\",\"operand\": [\"2018-01-01 00:00:00\", \"2018-12-31 23:59:59\"]}}";
        String user1 = executeRuleBuilder1.buildToJSONString();

        ExecuteRuleBuilder executeRuleBuilder2 = new ExecuteRuleBuilder();
        executeRuleBuilder2.addRuleExpression("gender", AssignmentOperatorType.IN, "female");
        executeRuleBuilder2.addRuleExpression("hobby", AssignmentOperatorType.IN, "travel", "book");
        executeRuleBuilder2.addRuleExpression("validDate", AssignmentOperatorType.BETWEEN, new Date[]{DateUtils.parseDate("2018-05-01 00:00:00", yyyyMMddHHmmss), DateUtils.parseDate("2018-12-31 23:59:59", yyyyMMddHHmmss)});
        //String user2 = "{\"gender\": {\"type\": \"String\",\"operator\": \"in\",\"operand\": [\"female\"]},\"hobby\": {\"type\": \"String\",\"operator\": \"in\",\"operand\": [\"travel\", \"book\"]},\"validDate\": {\"type\": \"Date\",\"operator\": \"bt\",\"operand\": [\"2018-05-01 00:00:00\", \"2018-12-31 23:59:59\"]}}";
        String user2 = executeRuleBuilder2.buildToJSONString();

        ExecuteRuleBuilder executeRuleBuilder3 = new ExecuteRuleBuilder();
        executeRuleBuilder3.addRuleExpression("gender", AssignmentOperatorType.IN, "male");
        executeRuleBuilder3.addRuleExpression("hobby", AssignmentOperatorType.IN, "travel", "book");
        //String user3 = "{\"gender\": {\"type\": \"String\",\"operator\": \"in\",\"operand\": [\"male\"]},\"hobby\": {\"type\": \"String\",\"operator\": \"in\",\"operand\": [\"travel\", \"book\"]}}";
        String user3 = executeRuleBuilder3.buildToJSONString();

        ExecuteRuleBuilder executeRuleBuilder4 = new ExecuteRuleBuilder();
        executeRuleBuilder4.addRuleExpression("gender", AssignmentOperatorType.IN, "male");
        executeRuleBuilder2.addRuleExpression("validDate", AssignmentOperatorType.BETWEEN, new Date[]{DateUtils.parseDate("2018-01-01 00:00:00", yyyyMMddHHmmss), DateUtils.parseDate("2018-12-31 23:59:59", yyyyMMddHHmmss)});
        //String user4 = "{\"gender\": {\"type\": \"String\",\"operator\": \"in\",\"operand\": [\"male\"]},\"validDate\": {\"type\": \"Date\",\"operator\": \"bt\",\"operand\": [\"2018-01-01 00:00:00\", \"2018-12-31 23:59:59\"]}}";
        String user4 = executeRuleBuilder4.buildToJSONString();

        ruleEngineService.add(ExecuteRule.build("rule1", user1, endDate));
        ruleEngineService.add(ExecuteRule.build("rule2", user2, endDate));
        ruleEngineService.add(ExecuteRule.build("rule3", user3, endDate));
        ruleEngineService.add(ExecuteRule.build("rule4", user4, endDate));

        String condition1 = "{\"gender\":\"male\",\"hobby\":\"travel\",\"validDate\":\"2018-06-01 00:00:00\"}";
        assertResult(condition1, 3);
        String condition2 = "{\"gender\":\"male\",\"hobby\":\"travel\"}";
        assertResult(condition2, 2);
    }

    private void assertResult(String condition, int rightCount) {
        List<ExecuteRule> result1 = ruleEngineService.match(condition);
        result1.forEach(rule ->{
            System.out.println(rule.getCode());
        });
        org.junit.Assert.assertTrue(result1.size() == rightCount);
    }
}
