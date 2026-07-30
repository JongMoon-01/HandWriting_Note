package com.handwritingnote.export;

/**
 * Export 모듈 진입점 마커.
 * 주석이 포함된 문서를 PDF 등으로 내보내는 job을 관리 (Kafka로 워커에 위임).
 * 실제 job 상태 추적 로직은 추후 구현.
 */
public final class ExportModule {
    private ExportModule() {
    }
}
