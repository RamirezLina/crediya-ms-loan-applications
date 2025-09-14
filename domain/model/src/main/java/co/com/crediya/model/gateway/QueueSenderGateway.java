package co.com.crediya.model.gateway;

import reactor.core.publisher.Mono;

public interface QueueSenderGateway {

    Mono<String> sendToNotification(String message);
     Mono<String> sendToCapacity(String message);
}
