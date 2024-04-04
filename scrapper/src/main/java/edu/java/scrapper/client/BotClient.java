package edu.java.scrapper.client;

import edu.java.scrapper.dto.bot.LinkUpdateRequest;
import io.github.resilience4j.retry.Retry;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

public class BotClient {
    private final Retry retry;
    private final WebClient webClient;

    public BotClient(String botBaseUrl, Retry retry) {
        this.retry = retry;
        this.webClient = WebClient.create(botBaseUrl);
    }

    public Void sendUpdates(LinkUpdateRequest updateRequest) {
        return Retry.decorateSupplier(retry, () -> webClient.post()
            .uri("/updates")
            .body(BodyInserters.fromValue(updateRequest))
            .retrieve()
            .bodyToMono(Void.class)
            .block())
            .get();
    }
}
