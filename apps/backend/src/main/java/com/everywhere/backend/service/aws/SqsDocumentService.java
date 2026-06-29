package com.everywhere.backend.service.aws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SqsDocumentService {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${SQS_QUEUE_URL:}")
    private String queueUrl;

    /**
     * Envia un mensaje a SQS para solicitar la generacion de un PDF.
     *
     * @param documentType tipo del documento (ej: "RECIBO", "DOCUMENTO_COBRANZA")
     * @param documentId   identificador del documento en la base de datos
     * @param data         datos adicionales para la generacion del PDF
     */
    public void sendPdfGenerationMessage(String documentType, Integer documentId, Map<String, Object> data) {
        if (queueUrl == null || queueUrl.isBlank()) {
            log.warn("SQS_QUEUE_URL no configurado, omitiendo envio de mensaje para: {} id={}", documentType, documentId);
            return;
        }

        Map<String, Object> messageBody = new LinkedHashMap<>();
        messageBody.put("type", documentType);
        messageBody.put("documentId", documentId);
        messageBody.put("data", data);
        messageBody.put("timestamp", Instant.now().toString());

        try {
            String jsonBody = objectMapper.writeValueAsString(messageBody);

            SendMessageRequest sendRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(jsonBody)
                    .build();

            SendMessageResponse response = sqsClient.sendMessage(sendRequest);

            log.info("Mensaje enviado a SQS para generacion de PDF: type={}, documentId={}, messageId={}",
                    documentType, documentId, response.messageId());

        } catch (JsonProcessingException e) {
            log.error("Error serializando mensaje SQS para {} id={}: {}", documentType, documentId, e.getMessage());
            throw new RuntimeException("Error al preparar mensaje para generacion de PDF", e);
        }
    }
}
