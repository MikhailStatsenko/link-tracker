package edu.java.scrapper.repository;

import edu.java.scrapper.model.Link;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface LinkRepository {
    List<Link> findAll();

    List<Link> findAllByChatId(long chatId);

    List<Link> findAllOutdatedLinks(long interval);

    Optional<Link> findByUrl(String url);

    Optional<Link> findByChatIdAndUrl(long chatId, String url);

    Link save(long chatId, Link link);

    void delete(long chatId, long linkId);

    void setLastCheckTime(Link link, OffsetDateTime lastCheckTime);
}
