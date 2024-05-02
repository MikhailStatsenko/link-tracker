package edu.java.bot.configuration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotEmpty
    String telegramToken,
    @NotNull
    Kafka kafka
) {
    public record Kafka(boolean useQueue, @NotEmpty List<String> bootstrapServers, @NotNull Consumer consumer,
                        @NotBlank String topicName, @NotBlank String dqlTopic,
                        @NotBlank String trustedPackages) {
        public record Consumer(@NotBlank String groupId, @NotBlank String mappings){
        }
    }
}
