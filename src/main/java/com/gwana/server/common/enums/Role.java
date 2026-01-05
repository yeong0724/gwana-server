package com.gwana.server.common.enums;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("관리자"),
    GENERAL("일반사용자");

    private final String description;

    Role(String description) {
        this.description = description;
    }
}
