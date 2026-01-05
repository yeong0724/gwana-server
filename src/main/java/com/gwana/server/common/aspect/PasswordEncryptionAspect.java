package com.gwana.server.common.aspect;

import com.gwana.server.common.annotation.PasswordEncryption;
import com.gwana.server.common.exception.SecurityException;
import com.gwana.server.common.security.EncryptService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Modifier;
import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class PasswordEncryptionAspect {
    private final EncryptService encryptService;

    @Around("execution(* com.gwana.server.controller..*.*(..))")
    public Object passwordEncryptionAspect(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Arrays.stream(proceedingJoinPoint.getArgs())
                .forEach(this::fieldEncryption);

        return proceedingJoinPoint.proceed();
    }

    public void fieldEncryption(Object object) {
        if (ObjectUtils.isEmpty(object)) {
            return;
        }

        FieldUtils.getAllFieldsList(object.getClass())
                .stream()
                .filter(filter -> !(Modifier.isFinal(filter.getModifiers()) && Modifier.isStatic(filter.getModifiers())))
                .forEach(field -> {
                    try {
                        boolean encryptionTarget = field.isAnnotationPresent(PasswordEncryption.class);
                        if (!encryptionTarget) {
                            return;
                        }

                        Object encryptionField = FieldUtils.readField(field, object, true);
                        if (!(encryptionField instanceof String)) {
                            return;
                        }

                        String encrypted = encryptService.encrypt((String) encryptionField);
                        FieldUtils.writeField(field, object, encrypted);
                    } catch (Exception e) {
                        throw new SecurityException.PasswordEncryptionException();
                    }
                });
    }
}
