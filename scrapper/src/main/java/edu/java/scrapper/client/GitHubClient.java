package edu.java.scrapper.client;

import edu.java.scrapper.dto.RepositoryResponse;

public interface GitHubClient {
    RepositoryResponse fetchRepository(String username, String repository);
}
