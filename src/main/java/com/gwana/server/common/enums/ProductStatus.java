package com.gwana.server.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductStatus {
    ON_SALE("판매중"),
    SOLD_OUT("품절"),
    HIDDEN("숨김"),
    DISCONTINUED("단종");

    private final String description;

    public static boolean isValid(String name) {
        if (name == null) {
            return false;
        }
        for (ProductStatus status : values()) {
            if (status.name().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
