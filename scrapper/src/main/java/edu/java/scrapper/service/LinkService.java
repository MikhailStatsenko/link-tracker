package edu.java.scrapper.service;

import edu.java.scrapper.model.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

public interface LinkService {
    List<Link> findAll(long chatId);

    List<Link> findAllOutdatedLinks(long interval);

    Link add(Long chatId, URI url);

    void setLastCheckTime(Link link, OffsetDateTime lastCheckTime);

    void remove(long chatId, URI url);
}
