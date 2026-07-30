package main

import (
	"log"
	"os"

	"github.com/JongMoon-01/HandWriting_Note/worker/internal/kafka"
)

func main() {
	bootstrapServers := getEnv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")

	log.Printf("handwriting-note worker starting (kafka=%s)", bootstrapServers)

	// TODO: kafka.NewConsumer(bootstrapServers, "document.ocr.requested")
	// TODO: OCR / PDF 렌더링 job 소비 루프 (internal/ocr 패키지 위임)
	// TODO: 네트워크 격리 컨테이너에서 실행 전제 (신뢰할 수 없는 파일 파싱)
	_ = kafka.Placeholder

	log.Println("worker scaffold ready — implement consumer loop next")
}

func getEnv(key, fallback string) string {
	if v := os.Getenv(key); v != "" {
		return v
	}
	return fallback
}
