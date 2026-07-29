# worker

Go로 작성된 비동기 워커. Kafka에서 OCR·PDF 렌더링 job을 소비해서 처리.
- Kafka client: segmentio/kafka-go (순수 Go, cgo 의존성 없음)
- OCR: PaddleOCR PP-OCRv5 (한글 모델)
- 신뢰할 수 없는 문서를 다루므로 네트워크 격리 컨테이너에서 실행
