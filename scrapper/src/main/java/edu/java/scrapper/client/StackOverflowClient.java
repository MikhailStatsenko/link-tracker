package edu.java.scrapper.client;

import edu.java.scrapper.dto.external.QuestionResponse;
import io.github.resilience4j.retry.Retry;
import org.springframework.web.reactive.function.client.WebClient;

public class StackOverflowClient {
    private final Retry retry;
    private final WebClient webClient;

    public StackOverflowClient(String stackOverflowBaseUrl, Retry retry) {
        this.retry = retry;
        this.webClient = WebClient.create(stackOverflowBaseUrl);
    }

    public QuestionResponse fetchQuestion(long questionId) {
        return Retry.decorateSupplier(retry, () -> webClient.get()
            .uri("/questions/{id}?site=stackoverflow", questionId)
            .retrieve()
            .bodyToMono(QuestionResponse.class)
            .block()).get();
    }
}
