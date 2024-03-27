package edu.java.scrapper.service.update.handler.stackoverflow;

import edu.java.scrapper.client.StackOverflowClient;
import edu.java.scrapper.dto.api.Update;
import edu.java.scrapper.dto.external.QuestionResponse;
import edu.java.scrapper.model.Link;
import edu.java.scrapper.service.update.handler.UpdateHandler;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StackoverflowUpdateHandler implements UpdateHandler {
    private static final String HOST = "stackoverflow.com";

    private final int questionIndex = 3;

    private final StackOverflowClient stackOverflowClient;

    @Override
    public String host() {
        return HOST;
    }

    @Override
    public List<Optional<Update>> fetchUpdates(Link link) {
        String url = link.getUrl().toString();
        long questionId = Long.parseLong(url.split("/")[questionIndex]);
        QuestionResponse response = stackOverflowClient.fetchQuestion(questionId);

        Optional<Update> update = Optional.empty();
        for (var item : response.items()) {
            if (item.lastActivityDate().isAfter(link.getLastCheckTime())) {
                update = Optional.of(new Update(
                    link.getId(),
                    url,
                    "Обновления в вопросе"
                ));
            }
        }
        return Collections.singletonList(update);
    }
}
