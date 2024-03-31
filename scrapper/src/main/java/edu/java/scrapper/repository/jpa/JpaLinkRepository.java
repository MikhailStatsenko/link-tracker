package edu.java.scrapper.repository.jpa;

import edu.java.scrapper.model.Link;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaLinkRepository extends JpaRepository<Link, Integer> {
    Optional<Link> findByUrl(URI url);

    List<Link> findAllByChatsId(long chatId);

    @Query("SELECT l FROM Link l WHERE CURRENT_TIMESTAMP - l.lastCheckTime >= :interval")
    List<Link> findAllOutdatedLinks(@Param("interval") Duration interval);

    @Query("SELECT l FROM Link l JOIN l.chats c WHERE c.id = :chatId AND l.url = :url")
    Optional<Link> findByChatIdAndUrl(@Param("chatId") long chatId, @Param("url") URI url);

    @Modifying
    @Query(value = "INSERT INTO chat_link (chat_id, link_id) VALUES (:chatId, :linkId)", nativeQuery = true)
    void addChatLinkRelation(@Param("chatId") long chatId, @Param("linkId") int linkId);

    @Modifying
    @Query(value = "DELETE FROM chat_link WHERE chat_id = :chatId AND link_id = :linkId", nativeQuery = true)
    void deleteChatLink(@Param("chatId") long chatId, @Param("linkId") int linkId);

    @Modifying
    @Query("DELETE FROM Link l WHERE l.id = :linkId "
         + "AND (SELECT COUNT(c) FROM Chat c JOIN c.trackedLinks tl WHERE tl.id = :linkId) = 0")
    void deleteIfNoChatsAssociated(@Param("linkId") int linkId);
}
