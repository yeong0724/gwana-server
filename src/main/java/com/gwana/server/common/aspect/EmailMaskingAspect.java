package com.gwana.server.common.aspect;

import com.gwana.server.common.annotation.EmailMasking;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Aspect
@Component
@RequiredArgsConstructor
public class EmailMaskingAspect {
    @AfterReturning(pointcut = "execution(* com.gwana.server.controller..*.*(..))", returning = "result")
    public void maskResponse(Object result) {
        if (result == null) return;
        applyMasking(result, new HashSet<>());
    }

    private void applyMasking(Object object, Set<Object> visited) {
        if (object == null) return;
        if (visited.contains(object)) return;
        visited.add(object);

        // List인 경우 각 요소에 적용
        if (object instanceof List<?> list) {
            list.forEach(item -> applyMasking(item, visited));
            return;
        }

        Class<?> clazz = object.getClass();

        // 기본 타입, Java 내장 타입은 스킵
        if (clazz.isPrimitive() || clazz.getName().startsWith("java.")) {
            return;
        }

        List<java.lang.reflect.Field> fields = FieldUtils.getAllFieldsList(clazz);

        // @EmailMasking이 있는 필드 처리
        for (java.lang.reflect.Field field : fields) {
            try {
                Object value = FieldUtils.readField(field, object, true);

                if (field.isAnnotationPresent(EmailMasking.class) && value instanceof String email) {
                    FieldUtils.writeField(field, object, maskEmail(email), true);
                } else if (value instanceof List<?> list) {
                    list.forEach(item -> applyMasking(item, visited));
                } else if (value != null && !isSimpleType(value)) {
                    applyMasking(value, visited);
                }
            } catch (Exception ignored) {
            }
        }
    }

    private boolean isSimpleType(Object obj) {
        Class<?> clazz = obj.getClass();
        return clazz.isPrimitive()
                || clazz.getName().startsWith("java.")
                || obj instanceof Number
                || obj instanceof Boolean
                || obj instanceof Character
                || obj instanceof String
                || clazz.isEnum()
                || clazz.isArray();
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        String[] parts = email.split("@");
        String id = parts[0];
        String domain = parts[1];
        String maskedId = id.substring(0, Math.min(3, id.length()))
                + "*".repeat(Math.max(id.length() - 3, 0));
        return maskedId + "@" + domain;
    }
}
