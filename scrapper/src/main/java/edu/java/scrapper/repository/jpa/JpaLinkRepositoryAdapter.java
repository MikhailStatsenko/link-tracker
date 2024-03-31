package edu.java.scrapper.repository.jpa;

import edu.java.scrapper.model.Link;
import edu.java.scrapper.repository.LinkRepository;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JpaLinkRepositoryAdapter implements LinkRepository {
    private final edu.java.scrapper.repository.jpa.JpaLinkRepository linkRepository;

    @Override
    public List<Link> findAll() {
        return linkRepository.findAll();
    }

    @Override
    public List<Link> findAllByChatId(long chatId) {
        return linkRepository.findAllByChatsId(chatId);
    }

    @Override
    public List<Link> findAllOutdatedLinks(long interval) {
        return linkRepository.findAllOutdatedLinks(Duration.ofNanos(interval));
    }

    @Override
    public Optional<Link> findByUrl(String url) {
        return linkRepository.findByUrl(URI.create(url));
    }

    @Override
    public Optional<Link> findByChatIdAndUrl(long chatId, String url) {
        return linkRepository.findByChatIdAndUrl(chatId, URI.create(url));
    }

    @Override
    @Transactional
    public Link save(long chatId, Link link) {
        Optional<Link> savedLink = linkRepository.findByUrl(link.getUrl());
        if (savedLink.isEmpty()) {
            linkRepository.save(link);
            savedLink = linkRepository.findByUrl(link.getUrl());
        }
        linkRepository.addChatLinkRelation(chatId, savedLink.get().getId());
        return savedLink.get();
    }

    @Override
    @Transactional
    public void delete(long chatId, int linkId) {
        linkRepository.deleteChatLink(chatId, linkId);
        linkRepository.deleteIfNoChatsAssociated(linkId);
    }

    @Override
    public void setLastCheckTime(Link link, OffsetDateTime lastCheckTime) {
        link.setLastCheckTime(lastCheckTime);
        linkRepository.save(link);
    }
}
