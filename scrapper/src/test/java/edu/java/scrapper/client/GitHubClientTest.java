package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.scrapper.dto.RepositoryResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.WebClientResponseException;
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
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        gitHubClient = new GitHubClient("http://localhost:" + wireMockServer.port());
        gitHubClient.init();
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void testFetchRepository() throws IOException {
        String repository = "link-tracker";
        String username = "MikhailStatsenko";
        File file = ResourceUtils.getFile("classpath:github-response-success.json");
        String expectedJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        wireMockServer
            .stubFor(get("/repos/%s/%s".formatted(username, repository))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody(expectedJson)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        RepositoryResponse response = gitHubClient.fetchRepository(username, repository);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(755042961L);
        assertThat(response.name()).isEqualTo("link-tracker");
        assertThat(response.fullName()).isEqualTo("MikhailStatsenko/link-tracker");
    }

    @Test
    public void testFetchRepositoryWithInvalidUrl() throws IOException {
        String repository = "invalid";
        String username = "invalid";
        File file = ResourceUtils.getFile("classpath:github-response-not-found.json");
        String expectedJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        wireMockServer
            .stubFor(get("/repos/%s/%s".formatted(username, repository))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withBody(expectedJson)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        assertThatThrownBy(() -> gitHubClient.fetchRepository(username, repository))
            .isInstanceOf(WebClientResponseException.class);
    }
}
