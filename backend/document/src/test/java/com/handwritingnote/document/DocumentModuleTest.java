package com.handwritingnote.document;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DocumentModuleTest {

    @Test
    void moduleLoads() {
        assertNotNull(DocumentModule.class);
    }
}
