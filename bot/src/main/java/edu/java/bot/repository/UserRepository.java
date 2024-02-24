package edu.java.bot.repository;

import edu.java.bot.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    List<User> findAll();

    Optional<User> findByChatId(Long chatId);

    void delete(User user);
}
