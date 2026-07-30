package com.handwritingnote.export;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExportModuleTest {

    @Test
    void moduleLoads() {
        assertNotNull(ExportModule.class);
    }
}
