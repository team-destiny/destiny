//package com.destiny.paymentservice.infrastructure.messaging.producer;
//
//import com.destiny.paymentservice.infrastructure.messaging.event.command.PaymentCommand;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class PaymentTestProducer {
//
//    private final KafkaTemplate<String, String> kafkaTemplate;
//    private final ObjectMapper objectMapper;
//
//    private static final String REQUEST_TOPIC = "payment-confirm-request";
//
//    public void sendTest(PaymentCommand command) {
//        try {
//            String json = objectMapper.writeValueAsString(command);
//            kafkaTemplate.send(REQUEST_TOPIC, json);
//            log.info("[PaymentValidateTestProducer] send to {}: {}", REQUEST_TOPIC, json);
//        } catch (Exception e) {
//            log.error("[PaymentValidateTestProducer] send failed: {}", e.getMessage(), e);
//        }
//    }
//}
