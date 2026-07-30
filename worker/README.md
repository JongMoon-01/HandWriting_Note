# worker

Go로 작성된 비동기 워커. Kafka에서 OCR·PDF 렌더링 job을 소비해서 처리.

## 스택
- Kafka client: `segmentio/kafka-go` (순수 Go, cgo 의존성 없음) — 아직 미설치, 아래 참고
- OCR: PaddleOCR PP-OCRv5 (한글 모델)
- 신뢰할 수 없는 문서를 다루므로 네트워크 격리 컨테이너에서 실행 전제

## 로컬 실행
```bash
go run ./cmd/worker
```

## 다음 단계
```bash
go get github.com/segmentio/kafka-go@latest
go mod tidy
```
그 다음 `internal/kafka/consumer.go`에 실제 Consumer 구현, `internal/ocr/ocr.go`에 PaddleOCR 연동 클라이언트 구현.
