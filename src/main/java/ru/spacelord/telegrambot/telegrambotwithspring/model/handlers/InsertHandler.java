package ru.spacelord.telegrambot.telegrambotwithspring.model.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.spacelord.telegrambot.telegrambotwithspring.Entities.WordsTable;
import ru.spacelord.telegrambot.telegrambotwithspring.model.BotState;
import ru.spacelord.telegrambot.telegrambotwithspring.services.UserService;
import ru.spacelord.telegrambot.telegrambotwithspring.services.WordsTableService;

import java.util.Locale;

@Component
public class InsertHandler {

    private final WordsTableService wordsTableService;
    private final UserService userService;

    @Autowired
    public InsertHandler(WordsTableService wordsTableService, UserService userService) {
        this.wordsTableService = wordsTableService;
        this.userService = userService;
    }

    public SendMessage insertHand(Message message, SendMessage sendMessage) {
        String[] words = message.getText().split("-");
        if(words.length==1) {
            sendMessage.setText("Неверный формат." + BotState.INSERTING.getDescription());
            return sendMessage;
        }
        if(words[0].contains("[^a-z]")) {
            sendMessage.setText("Неверный формат." + BotState.INSERTING.getDescription());
            return sendMessage;
        }
        wordsTableService.save(WordsTable.builder()
                .user(userService.getUserByChatId(message.getChatId()))
                .englishWord(words[0].trim().toLowerCase(Locale.ROOT))
                .russianWord(words[1].trim().toLowerCase(Locale.ROOT)).build());
        sendMessage.setText("Записал!");
        return sendMessage;
    }
}
