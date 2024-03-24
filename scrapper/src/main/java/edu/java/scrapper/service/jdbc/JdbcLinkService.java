package edu.java.scrapper.service.jdbc;

import edu.java.scrapper.exception.LinkAlreadyTrackedException;
import edu.java.scrapper.model.Link;
import edu.java.scrapper.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.service.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JdbcLinkService implements LinkService {
    private final JdbcLinkRepository linkRepository;

    @Override
    public List<Link> listAll(long chatId) {
        return linkRepository.findAllByChatId(chatId);
    }

    @Override
    public List<Link> listAllOutdatedLinks(long interval) {
        return linkRepository.findAllOutdatedLinks(interval);
    }

    @Override
    public Link add(Long chatId, URI url) {
        Optional<Link> link = linkRepository.findByChatIdAndUrl(chatId, url.toString());
        if (link.isPresent()) {
            throw new LinkAlreadyTrackedException();
        }
        return linkRepository.save(chatId, new Link(url));
    }

    @Override
    public void setLastCheckTime(Link link, OffsetDateTime lastCheckTime) {
        linkRepository.setLastCheckTime(link, lastCheckTime);
    }

    @Override
    public void remove(long chatId, URI url) {
        Link link = linkRepository.findByUrl(url.toString()).orElseThrow(LinkAlreadyTrackedException::new);
        linkRepository.delete(chatId, link.getId());
    }
}
