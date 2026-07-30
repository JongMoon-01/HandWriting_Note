package com.handwritingnote.annotation;

/**
 * Annotation 모듈 진입점 마커.
 * CQRS + Event Sourcing 적용 대상 — annotation_events(쓰기, append-only)와
 * annotations(읽기 프로젝션) 두 축으로 구성될 예정. 실제 이벤트 스토어/
 * 프로젝션 로직은 추후 구현.
 */
public final class AnnotationModule {
    private AnnotationModule() {
    }
}
