package edu.java.bot.client;

import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.dto.response.ApiErrorResponse;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.dto.response.ListLinksResponse;
import edu.java.bot.exception.ApiBadRequestException;
import edu.java.bot.exception.ApiNotFoundException;
import io.github.resilience4j.retry.Retry;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

public class ScrapperClient {
    private static final String TG_CHAT_ENDPOINT = "/tg-chat";
    private static final String LINKS_ENDPOINT = "/links";
    private static final String TG_CHAT_ID_HEADER = "Tg-Chat-Id";

    private final Retry retry;
    private final WebClient webClient;

    public ScrapperClient(String scrapperBaseUrl, Retry retry) {
        this.retry = retry;
        this.webClient = WebClient.create(scrapperBaseUrl);
    }

    public Void registerChat(Long chatId) {
        return Retry.decorateSupplier(retry, () -> webClient
            .post().uri(TG_CHAT_ENDPOINT + "/" + chatId)
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(ApiBadRequestException::new)
            )
            .bodyToMono(Void.class)
            .block()).get();
    }

    public Void deleteChat(Long chatId) {
        return Retry.decorateSupplier(retry, () -> webClient
            .delete().uri(TG_CHAT_ENDPOINT + "/" + chatId)
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(ApiBadRequestException::new)
            )
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(ApiNotFoundException::new)
            )
            .bodyToMono(Void.class)
            .block()).get();

    }

    public ListLinksResponse getLinks(Long chatId) {
        return Retry.decorateSupplier(retry, () -> webClient
            .get()
            .uri(LINKS_ENDPOINT)
            .header(TG_CHAT_ID_HEADER, String.valueOf(chatId))
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(ApiBadRequestException::new)
            )
            .bodyToMono(ListLinksResponse.class)
            .block()).get();
    }

    public LinkResponse addLink(Long chatId, AddLinkRequest request) {
        return Retry.decorateSupplier(retry, () -> webClient
            .post()
            .uri(LINKS_ENDPOINT)
            .header(TG_CHAT_ID_HEADER, String.valueOf(chatId))
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(ApiBadRequestException::new)
            )
            .bodyToMono(LinkResponse.class)
            .block()).get();
    }

    public LinkResponse deleteLink(Long chatId, RemoveLinkRequest request) {
        return Retry.decorateSupplier(retry, () -> webClient
            .method(HttpMethod.DELETE)
            .uri(LINKS_ENDPOINT)
            .header(TG_CHAT_ID_HEADER, String.valueOf(chatId))
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(ApiBadRequestException::new)
            )
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(ApiErrorResponse.class).map(ApiNotFoundException::new)
            )
            .bodyToMono(LinkResponse.class)
            .block()).get();
    }
}
