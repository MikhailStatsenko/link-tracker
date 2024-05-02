package edu.java.scrapper.configuration.repository;

import edu.java.scrapper.repository.ChatRepository;
import edu.java.scrapper.repository.LinkRepository;
import edu.java.scrapper.repository.jpa.JpaChatRepository;
import edu.java.scrapper.repository.jpa.JpaChatRepositoryAdapter;
import edu.java.scrapper.repository.jpa.JpaLinkRepository;
import edu.java.scrapper.repository.jpa.JpaLinkRepositoryAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaConfig {
    @Bean
    public LinkRepository jpaLinkRepositoryAdapter(JpaLinkRepository jpaLinkRepository) {
        return new JpaLinkRepositoryAdapter(jpaLinkRepository);
    }

    @Bean
    public ChatRepository jpaChatRepositoryAdapter(JpaChatRepository jpaChatRepository) {
        return new JpaChatRepositoryAdapter(jpaChatRepository);
    }
}
