package com.handwritingnote.annotation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AnnotationModuleTest {

    @Test
    void moduleLoads() {
        assertNotNull(AnnotationModule.class);
    }
}
