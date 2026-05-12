package com.gwana.server.dto;

import com.gwana.server.common.enums.Role;

public interface AuthAware {
    void setUserId(String userId);
    void setRole(Role role);
}