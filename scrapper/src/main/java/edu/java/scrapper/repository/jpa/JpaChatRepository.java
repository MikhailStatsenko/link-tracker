package edu.java.scrapper.repository.jpa;

import edu.java.scrapper.model.Chat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaChatRepository extends JpaRepository<Chat, Long> {
    int deleteChatById(Long chatId);

    @Query("SELECT c.id FROM Chat c JOIN c.trackedLinks l WHERE l.id = :linkId")
    List<Long> findAllChatIdsByLinkId(@Param("linkId") Long linkId);
}
