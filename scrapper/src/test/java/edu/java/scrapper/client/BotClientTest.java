package edu.java.scrapper.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.scrapper.dto.bot.LinkUpdateRequest;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

public class BotClientTest {
    private WireMockServer wireMockServer;
    private BotClient botClient;
    private ObjectMapper objectMapper;

    LinkUpdateRequest updateRequest = new LinkUpdateRequest(
        1,
        URI.create("https://example.com"),
        "Example description",
        List.of(2L, 3L)
    );

    @BeforeEach
    public void setUp() {
        wireMockServer = new WireMockServer(8888);
        wireMockServer.start();
        botClient = new BotClient("http://localhost:" + wireMockServer.port());
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void testSendUpdates() throws IOException {
        wireMockServer
            .stubFor(post("/updates")
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        Void response = botClient.sendUpdates(updateRequest);

        assertThat(response).isNull();
        wireMockServer.verify(postRequestedFor(urlEqualTo("/updates"))
            .withHeader(HttpHeaders.CONTENT_TYPE, matching(MediaType.APPLICATION_JSON_VALUE))
            .withRequestBody(equalToJson(objectMapper.writeValueAsString(updateRequest))));
    }
}

