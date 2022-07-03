package ru.spacelord.telegrambot.telegrambotwithspring.model.handlers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.spacelord.telegrambot.telegrambotwithspring.Entities.User;
import ru.spacelord.telegrambot.telegrambotwithspring.model.BotState;
import ru.spacelord.telegrambot.telegrambotwithspring.model.BotStateCash;
import ru.spacelord.telegrambot.telegrambotwithspring.model.Buttons;
import ru.spacelord.telegrambot.telegrambotwithspring.services.UserService;
import ru.spacelord.telegrambot.telegrambotwithspring.services.WordsTableService;

@Component
public class MessageHandler {

    private final Buttons buttons;
    private final UserService userService;
    private final BotStateCash botStateCash;
    private final ModeHandler modeHandler;
    private final WordsTableService wordsTableService;


    @Autowired
    public MessageHandler(UserService userService,
                          Buttons buttons,
                          BotStateCash botStateCash,
                          ModeHandler modeHandler,
                          WordsTableService wordsTableService) {
        this.userService = userService;
        this.buttons = buttons;
        this.botStateCash = botStateCash;
        this.modeHandler = modeHandler;
        this.wordsTableService = wordsTableService;
    }


    public BotApiMethod<?> handle(Message message, BotState botState) {
        Long chatId = message.getChatId();
        String userName = message.getFrom().getUserName();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        if(userService.isNotExist(chatId) && userName!=null) {
            userService.save(User.builder().points(0L).name(userName).chatId(chatId).build());
        }
        else if(userService.isNotExist(chatId) && userName==null) {
            userService.save(User.builder().points(0L).name(message.getFrom().getFirstName()).chatId(chatId).build());
        }
        switch (botState) {
            case START:
                sendMessage.setText(botState.getDescription());
                return buttons.createButtons(sendMessage,
                        "Английский->Русский","Русский->Английский",
                        "Создать свои слова","Удаление слов", "->");
            case HELP:
            case INFORMATION:
                sendMessage.setText(botState.getDescription());
                return sendMessage;
            case REMOVAL:
                if(wordsTableService.getAllByUser(chatId).size()==0) {
                    sendMessage.setText("У тебя еще нет слов, ты можешь их добавить в главном меню.");
                    return sendMessage;
                }
                sendMessage.setText(botState.getDescription() + "\n\n" + wordsTableService.getAllUserWords(chatId));
                botStateCash.saveBotState(chatId,BotState.REMOVAL);
                return buttons.createButtons(sendMessage,"Удалить все", "Выйти в главное меню");
            case INSERTING:
                sendMessage.setText(botState.getDescription());
                botStateCash.saveBotState(chatId,BotState.INSERTING);
                return buttons.createButtons(sendMessage,"Выйти в главное меню");
            case FIRST_MODE:
                botStateCash.saveBotState(chatId,BotState.FIRST_MODE);
                sendMessage.setText("Выбери, какие слова хочешь получить");
                return buttons.createButtons(sendMessage,"Случайные слова","Свои слова","Выйти в главное меню");
            case SECOND_MODE:
                botStateCash.saveBotState(chatId,BotState.SECOND_MODE);
                sendMessage.setText("Выбери, какие слова хочешь получить");
                return buttons.createButtons(sendMessage,"Случайные слова","Свои слова","Выйти в главное меню");
            case ACTIVE_FIRST_MODE_RAND:
            case ACTIVE_SECOND_MODE_RAND:
            case ACTIVE_FIRST_MODE_OWN:
            case ACTIVE_SECOND_MODE_OWN:
                return handleAnswer(message);
        }
        return sendMessage;
    }

    public BotApiMethod<?> handleAnswer(Message message) {
        if(botStateCash.isActiveFirstRand(message.getChatId())
                || botStateCash.isActiveFirstOwn(message.getChatId())) {
            return modeHandler.checkFirstAnswer(message);
        } else if(botStateCash.isActiveSecondRand(message.getChatId())
                || botStateCash.isActiveSecondOwn(message.getChatId())) {
            return modeHandler.checkSecondAnswer(message);
        }
        return null;
    }
}
