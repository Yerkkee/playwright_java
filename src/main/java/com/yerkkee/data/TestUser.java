package com.yerkkee.data;

import com.yerkkee.config.TestConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * SauceDemo demo accounts. Usernames are fixed by the application;
 * the password is resolved from configuration, never hardcoded in tests.
 */
@Getter
@RequiredArgsConstructor
public enum TestUser {
    STANDARD("standard_user"),
    LOCKED_OUT("locked_out_user"),
    PROBLEM("problem_user"),
    PERFORMANCE_GLITCH("performance_glitch_user"),
    ERROR("error_user"),
    VISUAL("visual_user");

    private final String username;

    public String getPassword() {
        return TestConfig.userPassword();
    }
}
