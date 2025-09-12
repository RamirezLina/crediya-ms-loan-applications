package co.com.crediya.model.gateway;

import reactor.core.publisher.Mono;

public interface QueueSenderGateway {

    Mono<String> send(String message);
}
