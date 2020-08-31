package com.webank.keymgr.config.db;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;

/**
 * @author aaronchu
 * @Description
 * @data 2020/08/31
 */
@Component
public class ExclusionDatabaseCondition implements Condition {

    public ExclusionDatabaseCondition(){}
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String propertyVal = context.getEnvironment().getProperty("system.mgrStyle", "file");
        return !"db".equals(propertyVal);
    }
}