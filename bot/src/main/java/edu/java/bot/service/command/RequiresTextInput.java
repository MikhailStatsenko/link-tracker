package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Update;

public interface RequiresTextInput {
    default boolean isInputGiven(Update update) {
        String text = update.message().text();
        return text != null && !text.strip().startsWith("/");
    }
}
