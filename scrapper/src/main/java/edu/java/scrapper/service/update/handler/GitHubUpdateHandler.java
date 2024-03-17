package edu.java.scrapper.service.update.handler;

import edu.java.scrapper.client.GitHubClient;
import edu.java.scrapper.dto.api.Update;
import edu.java.scrapper.dto.external.RepositoryResponse;
import edu.java.scrapper.model.Link;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GitHubUpdateHandler implements UpdateHandler {
    private static final String HOST = "github.com";

    private final int usernameIndex = 2;
    private final int repositoryIndex = 2;

    private final GitHubClient gitHubClient;

    @Override
    public String host() {
        return HOST;
    }

    @Override
    public Optional<Update> fetchUpdate(Link link) {
        String url = link.getUrl().toString();
        String[] urlParts = url.split("/");

        RepositoryResponse response = gitHubClient.fetchRepository(urlParts[usernameIndex], urlParts[repositoryIndex]);

        Optional<Update> update = Optional.empty();
        if (response.pushedAt().isAfter(link.getLastCheckTime())) {
            update = Optional.of(new Update(
                link.getId(),
                url,
                "Новый коммит",
                response.pushedAt())
            );
        }
        return update;
    }
}
