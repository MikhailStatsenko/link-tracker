package edu.java.scrapper.configuration.repository;

import edu.java.scrapper.repository.ChatRepository;
import edu.java.scrapper.repository.LinkRepository;
import edu.java.scrapper.repository.jooq.JooqChatRepository;
import edu.java.scrapper.repository.jooq.JooqLinkRepository;
import org.jooq.DSLContext;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
public class JooqConfig {
    @Bean
    public LinkRepository jooqLinkRepository(DSLContext dslContext) {
        return new JooqLinkRepository(dslContext);
    }

    @Bean
    public ChatRepository jooqChatRepository(DSLContext dslContext) {
        return new JooqChatRepository(dslContext);
    }
}
