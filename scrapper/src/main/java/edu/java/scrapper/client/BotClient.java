package edu.java.scrapper.client;

import edu.java.scrapper.dto.bot.LinkUpdateRequest;
import edu.java.scrapper.service.update.UpdateSender;
import io.github.resilience4j.retry.Retry;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

public class BotClient implements UpdateSender {
    private final Retry retry;
    private final WebClient webClient;

    public BotClient(String botBaseUrl, Retry retry) {
        this.retry = retry;
        this.webClient = WebClient.create(botBaseUrl);
    }

    public void sendUpdates(LinkUpdateRequest updateRequest) {
        Retry.decorateSupplier(retry, () -> webClient.post()
            .uri("/updates")
            .body(BodyInserters.fromValue(updateRequest))
            .retrieve()
            .bodyToMono(Void.class)
            .block())
            .get();
    }
}
