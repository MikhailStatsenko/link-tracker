package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.scrapper.dto.external.github.EventResponse;
import edu.java.scrapper.exception.NoSuchRepositoryException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class GitHubClientTest {
    private WireMockServer wireMockServer;
    private GitHubClient gitHubClient;

    @BeforeEach
    public void setUp() {
        wireMockServer = new WireMockServer(8888);
        wireMockServer.start();
        gitHubClient = new GitHubClient("http://localhost:" + wireMockServer.port(), "token", 10);
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void testFetchEventsWithInvalidUrl() throws IOException {
        String repository = "invalid";
        String username = "invalid";
        File file = ResourceUtils.getFile("classpath:github-response-not-found.json");
        String expectedJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        wireMockServer
            .stubFor(get("/repos/%s/%s/events?per_page=%d".formatted(username, repository, 10))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withBody(expectedJson)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        assertThatThrownBy(() -> gitHubClient.fetchEvents(username, repository))
            .isInstanceOf(NoSuchRepositoryException.class);
    }

    @Test
    public void testFetchEventsPullRequestEvent() throws IOException {
        String repository = "link-tracker";
        String username = "MikhailStatsenko";
        File file = ResourceUtils.getFile("classpath:github-response-pull-request-event.json");
        String expectedJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        wireMockServer
            .stubFor(get("/repos/%s/%s/events?per_page=%d".formatted(username, repository, 10))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(expectedJson)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        List<EventResponse> response = gitHubClient.fetchEvents(username, repository);

        EventResponse actual = response.getFirst();

        assertThat(response).isNotNull();
        assertThat(response).hasSize(1);
        assertThat(actual.getType()).isEqualTo("PullRequestEvent");
        assertThat(actual.getRepo().name()).isEqualTo("MikhailStatsenko/link-tracker");
        assertThat(actual.getActor().login()).isEqualTo("MikhailStatsenko");

        String mergeTo = actual.getPayload().path("pull_request").path("base").path("ref").asText();
        String mergeFrom = actual.getPayload().path("pull_request").path("head").path("ref").asText();
        assertThat(mergeTo).isEqualTo("master");
        assertThat(mergeFrom).isEqualTo("hw5");
    }

    @Test
    public void testFetchEventsPushtEvent() throws IOException {
        String repository = "link-tracker";
        String username = "MikhailStatsenko";
        File file = ResourceUtils.getFile("classpath:github-response-push-event.json");
        String expectedJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        wireMockServer
            .stubFor(get("/repos/%s/%s/events?per_page=%d".formatted(username, repository, 10))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(expectedJson)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        List<EventResponse> response = gitHubClient.fetchEvents(username, repository);

        EventResponse actual = response.getFirst();

        assertThat(response).isNotNull();
        assertThat(response).hasSize(1);
        assertThat(actual.getType()).isEqualTo("PushEvent");
        assertThat(actual.getRepo().name()).isEqualTo("MikhailStatsenko/link-tracker");
        assertThat(actual.getActor().login()).isEqualTo("MikhailStatsenko");
    }
}
