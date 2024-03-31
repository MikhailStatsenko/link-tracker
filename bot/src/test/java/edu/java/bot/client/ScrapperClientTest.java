package edu.java.bot.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.dto.response.ListLinksResponse;
import edu.java.bot.exception.ApiBadRequestException;
import edu.java.bot.exception.ApiNotFoundException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ScrapperClientTest {
    private WireMockServer wireMockServer;
    private ScrapperClient scrapperClient;

    private static final String TG_CHAT_ENDPOINT = "/tg-chat";
    private static final String LINKS_ENDPOINT = "/links";
    private static final String TG_CHAT_ID_HEADER = "Tg-Chat-Id";

    private final Long testChatId = 123L;
    private final Long testLinkId = 654L;
    private final String testUrl = "http://example.com";

    @BeforeEach
    public void setUp() {
        wireMockServer = new WireMockServer(8888);
        wireMockServer.start();
        scrapperClient = new ScrapperClient("http://localhost:" + wireMockServer.port());
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void testRegisterChat() {
        wireMockServer.stubFor(post(TG_CHAT_ENDPOINT + "/" + testChatId)
            .willReturn(aResponse()
                .withStatus(200)));

        assertThatCode(() -> scrapperClient.registerChat(testChatId))
            .doesNotThrowAnyException();
    }

    @Test
    public void testRegisterChatWhenBadRequest() throws IOException {
        File file = ResourceUtils.getFile("classpath:bad-request-response.json");
        String expectedJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        wireMockServer.stubFor(post(TG_CHAT_ENDPOINT + "/" + testChatId)
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader("Content-Type", "application/json")
                .withBody(expectedJson)));

        assertThatThrownBy(() -> scrapperClient.registerChat(testChatId))
            .isInstanceOf(ApiBadRequestException.class);
    }

    @Test
    public void testDeleteChat() {
        wireMockServer.stubFor(delete(TG_CHAT_ENDPOINT + "/" + testChatId)
            .willReturn(aResponse()
                .withStatus(200)));

        assertThatCode(() -> scrapperClient.deleteChat(123L))
            .doesNotThrowAnyException();
    }

    @Test
    public void testDeleteChatWhenBadRequest() throws IOException {
        File file = ResourceUtils.getFile("classpath:bad-request-response.json");
        String expectedJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        wireMockServer.stubFor(delete(TG_CHAT_ENDPOINT + "/" + testChatId)
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader("Content-Type", "application/json")
                .withBody(expectedJson)));

        assertThatThrownBy(() -> scrapperClient.deleteChat(testChatId))
            .isInstanceOf(ApiBadRequestException.class);
    }

    @Test
    public void testDeleteChatWhenNotFound() throws IOException {
        File file = ResourceUtils.getFile("classpath:not-found-response.json");
        String expectedJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        wireMockServer.stubFor(delete(TG_CHAT_ENDPOINT + "/" + testChatId)
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader("Content-Type", "application/json")
                .withBody(expectedJson)));

        assertThatThrownBy(() -> scrapperClient.deleteChat(testChatId))
            .isInstanceOf(ApiNotFoundException.class);
    }

    @Test
    public void testGetLinks() {
        wireMockServer.stubFor(get(LINKS_ENDPOINT)
            .withHeader(TG_CHAT_ID_HEADER, equalTo(testChatId.toString()))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(String.format("{\"links\": [{\"id\": %d, \"url\": \"%s\"}], \"size\": 1}", testLinkId, testUrl))));

        ListLinksResponse response = scrapperClient.getLinks(testChatId);

        assertThat(response).isNotNull();
        assertThat(response.links()).hasSize(1);
        assertThat(response.links().getFirst().id()).isEqualTo(testLinkId);
        assertThat(response.links().getFirst().url().toString()).isEqualTo(testUrl);
        assertThat(response.size()).isEqualTo(1);
    }

    @Test
    public void testGetLinksWhenBadRequest() throws IOException {
        File file = ResourceUtils.getFile("classpath:bad-request-response.json");
        String expectedJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        wireMockServer.stubFor(get(LINKS_ENDPOINT)
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader("Content-Type", "application/json")
                .withBody(expectedJson)));

        assertThatThrownBy(() -> scrapperClient.getLinks(testChatId))
            .isInstanceOf(ApiBadRequestException.class);
    }

    @Test
    public void testAddLink() {
        wireMockServer.stubFor(post(LINKS_ENDPOINT)
            .withHeader(TG_CHAT_ID_HEADER, equalTo(testChatId.toString()))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(String.format("{\"id\": %d, \"url\": \"%s\"}", testChatId, testUrl))));

        AddLinkRequest request = new AddLinkRequest(URI.create(testUrl));
        LinkResponse response = scrapperClient.addLink(testChatId, request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(testChatId);
        assertThat(response.url()).isEqualTo(URI.create(testUrl));
    }

    @Test
    public void testAddLinkWhenBadRequest() throws IOException {
        File file = ResourceUtils.getFile("classpath:bad-request-response.json");
        String expectedJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        wireMockServer.stubFor(post(LINKS_ENDPOINT)
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader("Content-Type", "application/json")
                .withBody(expectedJson)));

        AddLinkRequest request = new AddLinkRequest(URI.create(testUrl));
        assertThatThrownBy(() -> scrapperClient.addLink(testChatId,request))
            .isInstanceOf(ApiBadRequestException.class);
    }

    @Test
    public void testDeleteLink() {
        wireMockServer.stubFor(delete(LINKS_ENDPOINT)
            .withHeader(TG_CHAT_ID_HEADER, equalTo(testChatId.toString()))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(String.format("{\"id\": %d, \"url\": \"%s\"}", testChatId, testUrl))));

        RemoveLinkRequest request = new RemoveLinkRequest(URI.create(testUrl));
        LinkResponse response = scrapperClient.deleteLink(testChatId, request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(testChatId);
        assertThat(response.url()).isEqualTo(URI.create(testUrl));
    }

    @Test
    public void testDeleteLinkWhenBadRequest() throws IOException {
        File file = ResourceUtils.getFile("classpath:bad-request-response.json");
        String expectedJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        wireMockServer.stubFor(delete(LINKS_ENDPOINT)
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader("Content-Type", "application/json")
                .withBody(expectedJson)));

        RemoveLinkRequest request = new RemoveLinkRequest(URI.create(testUrl));
        assertThatThrownBy(() -> scrapperClient.deleteLink(testChatId, request))
            .isInstanceOf(ApiBadRequestException.class);
    }

    @Test
    public void testDeleteLinkWhenNotFound() throws IOException {
        File file = ResourceUtils.getFile("classpath:not-found-response.json");
        String expectedJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        wireMockServer.stubFor(delete(LINKS_ENDPOINT)
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader("Content-Type", "application/json")
                .withBody(expectedJson)));

        RemoveLinkRequest request = new RemoveLinkRequest(URI.create(testUrl));
        assertThatThrownBy(() -> scrapperClient.deleteLink(testChatId, request))
            .isInstanceOf(ApiNotFoundException.class);
    }
}
