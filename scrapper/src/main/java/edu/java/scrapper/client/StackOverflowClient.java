package edu.java.scrapper.client;

import edu.java.scrapper.dto.QuestionResponse;

public interface StackOverflowClient {
    QuestionResponse fetchQuestion(long questionId);
}
