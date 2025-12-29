package com.destiny.stockservice.infrastructure.event.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import com.destiny.stockservice.application.StockEventHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith(OutputCaptureExtension.class)
class StockConsumerDltLoggingTest {

    private static final String ANY_PAYLOAD = "{\"payload\":\"x\"}";

    private static final String ANY_EXCEPTION_MESSAGE = "boom";

    @ParameterizedTest
    @DisplayName("StockDltHandler가 알고 있는 dlt 수신시 로그 출력 테스트")
    @CsvSource({
        "stock-reservation-request, 재고 예약 요청 처리 실패",
        "stock-reservation-cancel, 재고 예약 취소 처리 실패",
        "stock-cancel-request, 확정 재고 취소 요청 처리 실패",
        "stock-create-message, 재고 생성 처리 실패",
        "order-create-success, 주문 완료(재고 커밋) 이벤트 처리 실패"
    })
    void handleStockDlt_logsExpectedMessage_forKnownTopics(
        String topic,
        String expectedErrorMessage,
        CapturedOutput output
    ) {
        StockConsumer consumer = newStockConsumer();

        handleStockDlt(consumer, topic);

        String combinedOutput = combinedOutput(output);
        assertThat(combinedOutput)
            .contains("DLT Topic")
            .contains("원본 메시지")
            .contains("예외 메시지")
            .contains(expectedErrorMessage);
    }

    @Test
    @DisplayName("StockDltHandler가 알 수 없는 dlt 토픽 수신시 로그 출력 테스트")
    void handleStockDlt_logsUnknownMessage_forUnknownTopic(CapturedOutput output) {
        StockConsumer consumer = newStockConsumer();

        handleStockDlt(consumer, "unknown-topic-dlt");

        assertThat(combinedOutput(output))
            .contains("알 수 없는 Stock 관련 DLT 메시지");
    }

    private static StockConsumer newStockConsumer() {
        StockEventHandler handler = Mockito.mock(StockEventHandler.class);
        return new StockConsumer(handler);
    }

    private static void handleStockDlt(StockConsumer consumer, String topic) {
        consumer.handleStockDlt(ANY_PAYLOAD, topic, ANY_EXCEPTION_MESSAGE);
    }

    private static String combinedOutput(CapturedOutput output) {
        return output.getOut() + output.getErr();
    }
}