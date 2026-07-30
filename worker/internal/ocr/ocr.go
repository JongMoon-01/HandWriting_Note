package ocr

// TODO: PaddleOCR PP-OCRv5 (한글 모델) 연동.
// Go에서 직접 모델을 서빙하기보다, 별도 Python/ONNX Runtime 추론 프로세스를
// 붙이고 이 패키지에서는 그 프로세스와 통신(gRPC 또는 HTTP)하는 클라이언트
// 역할만 담당하는 구조를 우선 고려.
