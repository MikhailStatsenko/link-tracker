package edu.java.scrapper.service.update;

import edu.java.scrapper.dto.api.Update;
import edu.java.scrapper.dto.bot.LinkUpdateRequest;
import edu.java.scrapper.exception.LinkNotSupportedException;
import edu.java.scrapper.model.Link;
import edu.java.scrapper.service.ChatService;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.update.handler.UpdateHandler;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateService {
    private final ChatService chatService;
    private final LinkService linkService;
    private final List<UpdateHandler> updateHandlers;

    public List<LinkUpdateRequest> fetchAllUpdates(long interval) {
        List<Link> links = linkService.listAllOutdatedLinks(interval);

        List<Update> updates = new ArrayList<>();
        for (var link : links) {
            updateHandlers.stream()
                .filter(handler -> handler.supports(link.getUrl()))
                .findAny()
                .orElseThrow(LinkNotSupportedException::new)
                .fetchUpdate(link)
                .ifPresent(upd -> {
                    updates.add(upd);
                    linkService.setLastCheckTime(link, OffsetDateTime.now());
                });
        }
        return updatesToLinkUpdateRequests(updates);
    }

    private List<LinkUpdateRequest> updatesToLinkUpdateRequests(List<Update> updates) {
        List<LinkUpdateRequest> linkUpdateRequests = new ArrayList<>();

        for (var update : updates) {
            List<Long> chatIds = chatService.listAllChatIdsByLinkId(update.linkId());
            linkUpdateRequests.add(new LinkUpdateRequest(
                update.linkId(),
                URI.create(update.url()),
                update.description(),
                chatIds
            ));
        }
        return linkUpdateRequests;
    }
}
