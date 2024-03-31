package edu.java.scrapper.repository.jooq;

import edu.java.scrapper.model.Link;
import edu.java.scrapper.repository.LinkRepository;
import edu.java.scrapper.repository.jooq.generated.tables.records.LinkRecord;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import static edu.java.scrapper.repository.jooq.generated.Tables.CHAT;
import static edu.java.scrapper.repository.jooq.generated.Tables.CHAT_LINK;
import static edu.java.scrapper.repository.jooq.generated.Tables.LINK;
import static org.jooq.impl.DSL.field;

@RequiredArgsConstructor
public class JooqLinkRepository implements LinkRepository {
    private final DSLContext dslContext;

    @Override
    public List<Link> findAll() {
        return dslContext
            .selectFrom(LINK)
            .fetchInto(Link.class);
    }

    @Override
    public List<Link> findAllByChatId(long chatId) {
        return dslContext
            .select(LINK.fields())
            .from(LINK)
            .join(CHAT_LINK)
            .on(LINK.ID.eq(CHAT_LINK.LINK_ID))
            .join(CHAT)
            .on(CHAT.ID.eq(CHAT_LINK.CHAT_ID))
            .where(CHAT.ID.eq(chatId))
            .fetchInto(Link.class);
    }

    @Override
    public List<Link> findAllOutdatedLinks(long interval) {
        Field<Long> intervalLastCheck = field(
            "EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - last_check_time))",
            Long.class
        );
        return dslContext
            .select(LINK.fields()).from(LINK)
            .where(intervalLastCheck.greaterOrEqual(interval))
            .or(LINK.LAST_CHECK_TIME.isNull())
            .fetchInto(Link.class);
    }


    @Override
    public Optional<Link> findByUrl(String url) {
        return dslContext
            .selectFrom(LINK)
            .where(LINK.URL.eq(url))
            .fetchOptionalInto(Link.class);
    }

    @Override
    public Optional<Link> findByChatIdAndUrl(long chatId, String url) {
        return dslContext
            .select(LINK.fields())
            .from(LINK)
            .join(CHAT_LINK)
            .on(LINK.ID.eq(CHAT_LINK.LINK_ID))
            .where(CHAT_LINK.CHAT_ID.eq(chatId))
            .and(LINK.URL.eq(url))
            .fetchOptionalInto(Link.class);
    }

    @Override
    public Link save(long chatId, Link link) {
        Optional<Link> savedLink = findByUrl(link.getUrl().toString());

        if (savedLink.isEmpty()) {
            LinkRecord linkRecord = dslContext
                .insertInto(LINK, LINK.URL, LINK.LAST_CHECK_TIME)
                .values(link.getUrl().toString(), link.getLastCheckTime())
                .returning().fetchOne();
            savedLink = Optional.of(linkRecord.into(Link.class));
        }

        dslContext.insertInto(CHAT_LINK, CHAT_LINK.CHAT_ID, CHAT_LINK.LINK_ID)
            .values(chatId, savedLink.get().getId()).execute();
        return savedLink.get();
    }

    @Override
    public void delete(long chatId, int linkId) {
        dslContext.deleteFrom(CHAT_LINK)
            .where(CHAT_LINK.CHAT_ID.eq(chatId))
            .and(CHAT_LINK.LINK_ID.eq(linkId))
            .execute();

        List<Long> chatIdsWithThisLink = dslContext
            .select(CHAT_LINK.CHAT_ID).from(CHAT_LINK)
            .where(CHAT_LINK.LINK_ID.eq(linkId))
            .fetchInto(Long.class);

        if (chatIdsWithThisLink.isEmpty()) {
            dslContext.deleteFrom(LINK).where(LINK.ID.eq(linkId)).execute();
        }
    }

    @Override
    public void setLastCheckTime(Link link, OffsetDateTime lastCheckTime) {
        dslContext
            .update(LINK)
            .set(LINK.LAST_CHECK_TIME, lastCheckTime)
            .where(LINK.ID.eq(link.getId()))
            .execute();
    }
}
