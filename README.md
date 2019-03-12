# 规则引擎

## 背景说明
本项目仅用于学习交流使用，第一版本只是使用了倒排索引及二分法实现了一个简单的规则匹配demo。   
当前仅支持规则新增及规则匹配，且使用本地内存维护规则列表。使用Netty时间轮控制规则的生效及失效。      
本项目未经允许不能用于商业用途。

## 目录结构
```
- algorithm：算法，包括倒排索引（等值匹配）和二分法查找（范围匹配）。
- contansts：应用中的常量。
- enums：枚举，通常用于定义规则结构的一些枚举。
- execption：异常类。
- factory：规则工厂，根据不同的操作符，选择不同的匹配算法对规则进行维护（新增或删除）、匹配。
- manager
- model：规则实体模型
    - Expression：规则每一项的结构，由type（数据类型），operator（操作符），operand（操作数）构成。
    - ExecuteRule：规则实体，由code（规则唯一标识），rule（规则内容），effectStartTime（规则有效期开始时间），effectEndTime（规则有效期结束时间）构成。
    - ExecuteRuleBuilder：ExecuteRule中的rule具体规则的构造器，内部会对规则结构做校验，生成符合规范的规则结构。
- service：对外接口定义。
- utils：工具类。
```

## 使用场景
适用规则匹配场景，场景举例：
>广告商需要根据客户身上的信息对客户进行针对性的广告投放。对于每一个广告，可以指定若干规则，客户身上的属性即为条件，条件与每一个规则匹配，一条规则中的每一项都符合，即为规则命中，这条规则对应的广告即可给用户投放。

## 使用说明
1.引入依赖
```
<dependency>
    <groupId>com.vey</groupId>
    <artifactId>rule-engine</artifactId>
    <version>1.0.0-RELEASE</version>
</dependency>
```

2.定义配置类
```
@Configuration
public class AppConfig {
    @Bean
    public RuleEngineService ruleEngineService() {
        RuleEngineService service = new RuleEngineService();
        return service;
    }
}
```

3.规则构建  
- 规则结构
```
{
    "gender" :
    {
        "type" : "String",
        "operator" : "in",
        "operand" : ["male"]
    },
    "hobby" :
    {
        "type" : "String",
        "operator" : "in",
        "operand" : ["travel","book"]
    },
    "validDate" :
    {
        "type" : "Date",
        "operator" : "bt",
        "operand" : ["2018-01-01 00:00:00","2018-12-31 23:59:59 23:59:59"]
    },
    ...
}
```

- *type*：数据类型，对应*AssignmentType*枚举
- *operator*：操作符，对应*AssignmentOperatorType*枚举
- *operand*：操作数，数组表示，根据不同的*AssignmentOperatorType*允许的操作数个数不一致。

- *AssignmentType*：规则支持的数据类型
```
String
Integer
Long
Date：日期需要范围匹配时，会转换成时间戳数值类型再做比较
```
- *AssignmentOperatorType*：规则支持的操作方式
```
bt：闭区间（范围匹配），operand有且有两个操作数，且不支持String类型
in：多个等值匹配，只需满足其中一个，operand有任意个（大于等于1）操作数
gt：大于（范围匹配），operand仅支持一个操作数，且不支持String类型
lt：小于（范围匹配），operand仅支持一个操作数，且不支持String类型
```

- 规则构建：使用*ExecuteRuleBuilder*构建规则，该构造器会对规则格式做检验。
```
ExecuteRuleBuilder executeRuleBuilder = new ExecuteRuleBuilder();
executeRuleBuilder.addRuleExpression("gender", AssignmentOperatorType.IN, "male");
executeRuleBuilder.addRuleExpression("hobby", AssignmentOperatorType.IN, "travel", "film");
executeRuleBuilder.addRuleExpression("validDate", AssignmentOperatorType.BETWEEN, new Date[]{DateUtils.parseDate("2018-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"), DateUtils.parseDate("2018-12-31 23:59:59", "yyyy-MM-dd HH:mm:ss")});
String user1 = executeRuleBuilder.buildToJSONString();
```

