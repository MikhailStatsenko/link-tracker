package edu.java.scrapper.client;

import edu.java.scrapper.dto.QuestionResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class StackOverflowClientImpl implements StackOverflowClient {
    private String stackOverflowBaseUrl = "https://api.stackexchange.com/2.3";
    private final WebClient webClient;

    public StackOverflowClientImpl(String stackOverflowBaseUrl) {
        if (stackOverflowBaseUrl != null && !stackOverflowBaseUrl.isBlank()) {
            this.stackOverflowBaseUrl = stackOverflowBaseUrl;
        }
        this.webClient = WebClient.builder().baseUrl(this.stackOverflowBaseUrl).build();
    }

    @Override
    public QuestionResponse fetchQuestion(long questionId) {
        return webClient.get()
            .uri("/questions/{id}?site=stackoverflow", questionId)
            .retrieve()
            .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(RuntimeException.class)
                .flatMap(e -> Mono.error(new RuntimeException("Stack Overflow API exception"))))
            .bodyToMono(QuestionResponse.class)
            .block();
    }
}
