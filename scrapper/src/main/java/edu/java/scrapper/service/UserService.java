package edu.java.scrapper.service;

import edu.java.scrapper.exception.LinkAlreadyTrackedException;
import edu.java.scrapper.exception.LinkNotFoundException;
import edu.java.scrapper.exception.UserAlreadyExistsException;
import edu.java.scrapper.exception.UserNotFoundException;
import edu.java.scrapper.model.Link;
import edu.java.scrapper.model.User;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private static long linkId = 0;
    private final Map<Long, User> userRepository = new HashMap<>();

    public Link addLink(Long chatId, URI url) {
        User user = userRepository.get(chatId);
        if (findLink(user, url).isPresent()) {
            throw new LinkAlreadyTrackedException();
        }
        var link = new Link(url);
        link.setId(linkId++);
        user.getTrackedLinks().add(link);

        return link;
    }

    public void deleteLink(long chatId, URI url) {
        boolean isRemoved = userRepository.get(chatId)
            .getTrackedLinks()
            .removeIf(link -> link.getUrl().equals(url));
        if (!isRemoved) {
            throw new LinkNotFoundException();
        }
    }

    public List<Link> getLinks(long chatId) {
        return userRepository.get(chatId).getTrackedLinks();
    }

    public void deleteUser(Long id) {
        User deletedUser = userRepository.remove(id);
        if (deletedUser == null) {
            throw new UserNotFoundException();
        }
    }

    private Optional<Link> findLink(User user, URI link) {
        return user.getTrackedLinks().stream()
            .filter(l -> l.getUrl().equals(link))
            .findAny();
    }

    public void addUser(Long id) {
        var user = userRepository.get(id);
        if (user != null) {
            throw new UserAlreadyExistsException();
        }
        userRepository.put(id, new User(id, new ArrayList<>()));
    }
}
