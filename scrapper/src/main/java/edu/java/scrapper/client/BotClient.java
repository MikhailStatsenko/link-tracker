package edu.java.scrapper.client;

import edu.java.scrapper.dto.bot.LinkUpdateRequest;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

public class BotClient {
    private final WebClient webClient;

    public BotClient(String botBaseUrl) {
        this.webClient = WebClient.create(botBaseUrl);
    }

    public Void sendUpdates(LinkUpdateRequest updateRequest) {
        return webClient.post()
            .uri("/updates")
            .body(BodyInserters.fromValue(updateRequest))
            .retrieve()
            .bodyToMono(Void.class)
            .block();
    }
}
