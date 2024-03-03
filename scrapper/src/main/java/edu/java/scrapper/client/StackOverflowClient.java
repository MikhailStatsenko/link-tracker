package edu.java.scrapper.client;

import edu.java.scrapper.dto.external.QuestionResponse;
import org.springframework.web.reactive.function.client.WebClient;

public class StackOverflowClient {
    private final WebClient webClient;

    public StackOverflowClient(String stackOverflowBaseUrl) {
        this.webClient = WebClient.create(stackOverflowBaseUrl);
    }

    public QuestionResponse fetchQuestion(long questionId) {
        return webClient.get()
            .uri("/questions/{id}?site=stackoverflow", questionId)
            .retrieve()
            .bodyToMono(QuestionResponse.class)
            .block();
    }
}
