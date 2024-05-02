package edu.java.bot.controller;

import edu.java.bot.dto.request.LinkUpdateRequest;
import edu.java.bot.dto.response.ApiErrorResponse;
import edu.java.bot.service.UpdateService;
import io.micrometer.core.annotation.Counted;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class BotController {
    private final UpdateService updateService;

    @Operation(
        summary = "Отправить обновление",
        responses = {
            @ApiResponse(responseCode = "200", description = "Обновление обработано"),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            }),
            @ApiResponse(responseCode = "429", description = "Превышен лимит запросов", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            })
        }
    )
    @Counted(value = "api_counter", description = "Amount of requests to API")
    @PostMapping("/updates")
    public ResponseEntity<Void> processUpdate(@Valid @RequestBody LinkUpdateRequest linkUpdateRequest) {
        updateService.processUpdate(linkUpdateRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
