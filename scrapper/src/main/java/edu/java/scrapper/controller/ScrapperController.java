package edu.java.scrapper.controller;

import edu.java.scrapper.dto.api.request.AddLinkRequest;
import edu.java.scrapper.dto.api.request.RemoveLinkRequest;
import edu.java.scrapper.dto.api.response.ApiErrorResponse;
import edu.java.scrapper.dto.api.response.LinkResponse;
import edu.java.scrapper.dto.api.response.ListLinksResponse;
import edu.java.scrapper.model.Link;
import edu.java.scrapper.service.ChatService;
import edu.java.scrapper.service.LinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class ScrapperController {
    private final ChatService chatService;
    private final LinkService linkService;

    @Operation(
        summary = "Убрать отслеживание ссылки",
        responses = {
            @ApiResponse(responseCode = "200", description = "Ссылка успешно убрана", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = LinkResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            }),
            @ApiResponse(responseCode = "404", description = "Ссылка не найдена", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            })
        }
    )
    @DeleteMapping("/links")
    public ResponseEntity<LinkResponse> deleteLink(
        @NotNull @RequestHeader(value = "Tg-Chat-Id") Long tgChatId,
        @Valid @RequestBody RemoveLinkRequest removeLinkRequest
    ) {
        linkService.remove(tgChatId, removeLinkRequest.link());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
        summary = "Получить все отслеживаемые ссылки",
        responses = {
            @ApiResponse(responseCode = "200", description = "Ссылки успешно получены", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ListLinksResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            })
        }
    )
    @GetMapping("/links")
    ResponseEntity<ListLinksResponse> getLinks(@NotNull @RequestHeader(value = "Tg-Chat-Id") Long tgChatId) {
        List<Link> links = linkService.findAll(tgChatId);
        List<LinkResponse> linksResponses = links.stream().map(
            link -> new LinkResponse(link.getId(), link.getUrl())
        ).toList();
        return new ResponseEntity<>(
            new ListLinksResponse(linksResponses, linksResponses.size()),
            HttpStatus.OK
        );
    }

    @Operation(
        operationId = "linksPost",
        summary = "Добавить отслеживание ссылки",
        responses = {
            @ApiResponse(responseCode = "200", description = "Ссылка успешно добавлена", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = LinkResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            })
        }
    )
    @PostMapping("/links")
    public ResponseEntity<LinkResponse> addLink(
        @NotNull @RequestHeader(value = "Tg-Chat-Id") Long tgChatId,
        @Valid @RequestBody AddLinkRequest addLinkRequest
    ) {
        Link link = linkService.add(tgChatId, addLinkRequest.link());
        return new ResponseEntity<>(
            new LinkResponse(link.getId(), link.getUrl()),
            HttpStatus.OK
        );
    }

    @Operation(
        summary = "Удалить чат",
        responses = {
            @ApiResponse(responseCode = "200", description = "Чат успешно удалён"),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            }),
            @ApiResponse(responseCode = "404", description = "Чат не существует", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            })
        }
    )
    @DeleteMapping("/tg-chat/{id}")
    public ResponseEntity<Void> deleteChat(
        @PathVariable Long id
    ) {
        chatService.unregister(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Operation(
        summary = "Зарегистрировать чат",
        responses = {
            @ApiResponse(responseCode = "200", description = "Чат зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            })
        }
    )
    @PostMapping("/tg-chat/{id}")
    public ResponseEntity<Void> addChat(
        @PathVariable Long id
    ) {
        chatService.register(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
