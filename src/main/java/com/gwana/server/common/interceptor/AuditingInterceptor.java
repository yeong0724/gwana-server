package com.gwana.server.common.interceptor;

import com.gwana.server.common.security.JwtTokenProvider;
import com.gwana.server.dto.user.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.mapping.SqlCommandType;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class AuditingInterceptor implements Interceptor {
    private final JwtTokenProvider jwtTokenProvider;

    private static final String CREATED_AT = "createdAt";
    private static final String CREATED_BY = "createdBy";
    private static final String MODIFIED_AT = "modifiedAt";
    private static final String MODIFIED_BY = "modifiedBy";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];

        if (parameter != null && hasAuditFields(parameter)) {
            SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

            String currentUser = null;
            AuthUser authUser = jwtTokenProvider.getUserInfo();
            if (authUser != null) currentUser = authUser.getUserId();

            if (sqlCommandType == SqlCommandType.INSERT) {
                setFieldValue(parameter, CREATED_AT, LocalDateTime.now());
                setFieldValue(parameter, CREATED_BY, currentUser);
            }

            if (sqlCommandType == SqlCommandType.INSERT || sqlCommandType == SqlCommandType.UPDATE) {
                setFieldValue(parameter, MODIFIED_AT, LocalDateTime.now());
                setFieldValue(parameter, MODIFIED_BY, currentUser);
            }
        }

        return invocation.proceed();
    }

    private boolean hasAuditFields(Object obj) {
        Class<?> clazz = obj.getClass();
        return findField(clazz, CREATED_AT) != null || findField(clazz, MODIFIED_AT) != null;
    }

    private void setFieldValue(Object obj, String fieldName, Object value) {
        try {
            Field field = findField(obj.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                field.set(obj, value);
                log.debug("Set {}={} in {}", fieldName, value, obj.getClass().getSimpleName());
            }
        } catch (IllegalAccessException e) {
            log.warn("Failed to set field '{}' in {}: {}", fieldName, obj.getClass().getSimpleName(), e.getMessage());
        }
    }

    private Field findField(Class<?> clazz, String fieldName) {
        while (clazz != null && clazz != Object.class) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }
}
