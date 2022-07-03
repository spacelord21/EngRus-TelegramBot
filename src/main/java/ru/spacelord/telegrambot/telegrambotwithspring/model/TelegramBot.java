package ru.spacelord.telegrambot.telegrambotwithspring.model;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;


@Getter
@Setter
public class TelegramBot extends SpringWebhookBot {

    private String botPath;
    private String botUsername;
    private String botToken;

    private TelegramFacade telegramFacade;

    public TelegramBot(SetWebhook setWebhook, TelegramFacade telegramFacade) {
        super(setWebhook);
        this.telegramFacade = telegramFacade;
    }

    public TelegramBot(DefaultBotOptions options, SetWebhook setWebhook, TelegramFacade telegramFacade) {
        super(options, setWebhook);
        this.telegramFacade = telegramFacade;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return telegramFacade.handleUpdate(update);
    }
}
