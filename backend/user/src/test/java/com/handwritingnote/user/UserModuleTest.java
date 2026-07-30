package com.handwritingnote.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserModuleTest {

    @Test
    void moduleLoads() {
        assertNotNull(UserModule.class);
    }
}
