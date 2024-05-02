package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.scrapper.configuration.retry.ClientRetryConfig;
import edu.java.scrapper.configuration.retry.RetryPolicy;
import edu.java.scrapper.dto.external.QuestionResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StackOverflowClientTest {
    private WireMockServer wireMockServer;
    private StackOverflowClient stackOverflowClient;
    private ClientRetryConfig.StackOverflowClientRetryConfig retryConfig;

    @BeforeEach
    public void setUp() {
        wireMockServer = new WireMockServer(8888);
        wireMockServer.start();

        retryConfig = new ClientRetryConfig.StackOverflowClientRetryConfig(
            ClientRetryConfig.RetryProperties.builder()
                .retryPolicy(RetryPolicy.CONSTANT)
                .maxAttempts(3)
                .waitDuration(2)
                .codes(Set.of(HttpStatus.INTERNAL_SERVER_ERROR))
                .build()
        );

        stackOverflowClient = new StackOverflowClient("http://localhost:" + wireMockServer.port(), retryConfig.getRetry());
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void testFetchRepository() throws IOException {
        long questionId = 5905054L;
        File file = ResourceUtils.getFile("classpath:stackoverflow-response-success.json");
        String expectedJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        wireMockServer
            .stubFor(get("/questions/%d?site=stackoverflow".formatted(questionId))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(expectedJson)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        QuestionResponse response = stackOverflowClient.fetchQuestion(questionId);

        assertThat(response).isNotNull();
        assertThat(response.items()).isNotEmpty();
        assertThat(response.items().getFirst().questionId()).isEqualTo(5905054);
    }

    @Test
    public void testFetchRepositoryWithInvalidUrl() throws IOException {
        long badQuestionId = Long.MAX_VALUE;

        File file = ResourceUtils.getFile("classpath:stackoverflow-response-bad-parameter.json");
        String expectedJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        wireMockServer
            .stubFor(get("/questions/%d?site=stackoverflow".formatted(badQuestionId))
                .willReturn(aResponse()
                    .withStatus(400)
                    .withBody(expectedJson)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));


        assertThatThrownBy(() -> stackOverflowClient.fetchQuestion(badQuestionId))
            .isInstanceOf(WebClientResponseException.class);
    }
}
