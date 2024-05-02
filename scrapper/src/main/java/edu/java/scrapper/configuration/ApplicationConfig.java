package edu.java.scrapper.configuration;

import edu.java.scrapper.configuration.repository.AccessType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.annotation.Validated;

@Validated
@EnableScheduling
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotNull
    LinksScheduler linksScheduler,
    @NotNull
    AccessType databaseAccessType,
    @NotNull
    Kafka kafka,
    boolean useQueue
) {
    public record LinksScheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }

    public record Kafka(@NotEmpty List<String> bootstrapServers, @NotBlank String topicName) {
    }
}
