package co.com.crediya.sqs.sender;

import co.com.crediya.model.error.BusinessException;
import co.com.crediya.model.gateway.QueueSenderGateway;
import co.com.crediya.sqs.sender.config.SQSSenderProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements QueueSenderGateway {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;


    public Mono<String> sendToNotification(String message) {
        return Mono.fromCallable(properties::queueNotificationsUrl)
                .flatMap(url-> send(message, url))
                .onErrorMap(ex-> BusinessException.Type.NOTIFICATION_SEND_FAILED.build(ex.getMessage()));
    
    }

    public Mono<String> sendToCapacity(String message) {
        return Mono.fromCallable(properties::queueDebtCapacityUrl)
                .flatMap(url-> send(message, url))
                .onErrorMap(ex-> BusinessException.Type.DEBT_CAPACITY_SEND_FAILED.build(ex.getMessage()));

    }
    
    private Mono<String> send(String message, String queueUrl) {
        return Mono.fromCallable(() -> buildRequest(message, queueUrl))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.info("Message sent {}", message)) //TODO borrar
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId)
                .doOnError(this::logError);
              
    }

    private SendMessageRequest buildRequest(String message, String queueUrl) {
        return SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .build();
    }

    private void logError(Throwable exception) {
        log.error("Error Message: {} \n Stack trace: {}", exception.getMessage(), exception.getStackTrace());
    }
}
