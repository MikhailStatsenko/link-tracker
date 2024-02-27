package edu.java.scrapper.client;

import edu.java.scrapper.dto.QuestionResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

public class StackOverflowClient {
    @Value("${api.default-url.stackoverflow}")
    private String stackOverflowDefaultUrl;
    private String stackOverflowBaseUrl;
    private WebClient webClient;

    public StackOverflowClient(String stackOverflowBaseUrl) {
        this.stackOverflowBaseUrl = stackOverflowBaseUrl;
    }

    @PostConstruct
    public void init() {
        if (stackOverflowBaseUrl == null || stackOverflowBaseUrl.isBlank()) {
            stackOverflowBaseUrl = stackOverflowDefaultUrl;
        }
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
