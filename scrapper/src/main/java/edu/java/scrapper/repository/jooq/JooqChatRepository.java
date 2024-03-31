package edu.java.scrapper.repository.jooq;

import edu.java.scrapper.model.Chat;
import edu.java.scrapper.repository.ChatRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import static edu.java.scrapper.repository.jooq.generated.Tables.CHAT;
import static edu.java.scrapper.repository.jooq.generated.Tables.CHAT_LINK;

@RequiredArgsConstructor
public class JooqChatRepository implements ChatRepository {
    private final DSLContext dslContext;

    @Override
    public List<Chat> findAll() {
        return dslContext
            .selectFrom(CHAT)
            .fetchInto(Chat.class);
    }

    @Override
    public List<Long> findAllChatIdsByLinkId(long linkId) {
        return dslContext
            .select(CHAT_LINK.CHAT_ID)
            .from(CHAT_LINK)
            .where(CHAT_LINK.LINK_ID.eq((int) linkId))
            .fetchInto(Long.class);
    }

    @Override
    public Optional<Chat> findById(long chatId) {
        return dslContext
            .selectFrom(CHAT)
            .where(CHAT.ID.eq(chatId))
            .fetchOptionalInto(Chat.class);
    }

    @Override
    public void save(Chat chat) {
        dslContext
            .insertInto(CHAT)
            .values(chat.getId())
            .execute();
    }

    @Override
    public int delete(long chatId) {
        return dslContext
            .deleteFrom(CHAT)
            .where(CHAT.ID.eq(chatId))
            .execute();
    }
}

