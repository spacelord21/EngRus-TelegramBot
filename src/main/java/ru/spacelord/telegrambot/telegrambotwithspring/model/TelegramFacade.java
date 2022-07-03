package ru.spacelord.telegrambot.telegrambotwithspring.model;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.spacelord.telegrambot.telegrambotwithspring.model.handlers.InsertHandler;
import ru.spacelord.telegrambot.telegrambotwithspring.model.handlers.ModeHandler;
import ru.spacelord.telegrambot.telegrambotwithspring.model.handlers.MessageHandler;
import ru.spacelord.telegrambot.telegrambotwithspring.model.parser.Word;
import ru.spacelord.telegrambot.telegrambotwithspring.services.UserService;
import ru.spacelord.telegrambot.telegrambotwithspring.services.WordsTableService;


@Component
public class TelegramFacade {

    private final MessageHandler messageHandler;
    private final BotStateCash botStateCash;
    private final ModeHandler modeHandler;
    private final UserService userService;
    private final Buttons buttons;
    private final WordsTableService wordsTableService;
    private final InsertHandler insertHandler;

    @Autowired
    public TelegramFacade(MessageHandler messageHandler,
                          BotStateCash botStateCash,
                          ModeHandler modeHandler,
                          UserService userService,
                          Buttons buttons,
                          WordsTableService wordsTableService,
                          InsertHandler insertHandler) {
        this.messageHandler = messageHandler;
        this.botStateCash = botStateCash;
        this.modeHandler = modeHandler;
        this.userService = userService;
        this.buttons = buttons;
        this.wordsTableService = wordsTableService;
        this.insertHandler = insertHandler;
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        if(update.hasCallbackQuery()) {
            return handleInputCallbackData(update);
        }
        else {
            Message message = update.getMessage();
            if(message.hasText()) {
                return handleInputMessage(message);
            }
        }
        return null;
    }

    public BotApiMethod<?> handleInputCallbackData(Update update) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setText(update.getCallbackQuery().getData());
        answerCallbackQuery.setCallbackQueryId(update.getCallbackQuery().getId());
        answerCallbackQuery.setShowAlert(true);
        return answerCallbackQuery;
    }

    public BotApiMethod<?> handleInputMessage(Message message) {
        String inputMessage = message.getText();
        Long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        BotState botState;
        switch (inputMessage) {
            case ("/start"), ("Выйти в главное меню") ->  {
                botState = BotState.START;
                botStateCash.delete(chatId);
            }
            case ("Помощь") -> botState = BotState.HELP;
            case ("Английский->Русский") -> botState = BotState.FIRST_MODE;
            case ("Русский->Английский") -> botState = BotState.SECOND_MODE;
            case ("Информация") -> botState = BotState.INFORMATION;
            case ("Топ пользователей") -> {
                sendMessage.setText(userService.topTen());
                return sendMessage;
            }
            case ("Удаление слов") -> botState = BotState.REMOVAL;
            case ("->") -> {
                sendMessage.setText("->");
                return buttons.createButtons(sendMessage,"Топ пользователей", "Информация","Помощь", "<-");
            }
            case ("<-") -> botState = BotState.START;
            case ("Создать свои слова") -> botState = BotState.INSERTING;
            case ("Случайные слова") -> {
                return modeHandler.randomWordsCase(chatId,sendMessage);
            }
            case ("Свои слова") -> {
                return modeHandler.ownWordsCase(chatId,sendMessage);
            }
            case ("Готов!") -> {
                if(botStateCash.getBotStateByChatId(chatId)==null) {
                    sendMessage.setText("Ошибка!"+Emojis.WRONG.getString()+ "Попробуй сначала /start");
                    return sendMessage;
                }
                Word word = modeHandler.getWordFromMap(chatId);
                BotState botState1 = botStateCash.getBotStateByChatId(chatId);
                if(botState1.equals(BotState.ACTIVE_FIRST_MODE_RAND)
                        || botState1.equals(BotState.ACTIVE_FIRST_MODE_OWN)) {
                    sendMessage.setText(word.getEnglishWord());
                    sendMessage.setReplyMarkup(buttons.createInlineHint(word.getRussianWordToString()));
                    return sendMessage;
                } else if(botState1.equals(BotState.ACTIVE_SECOND_MODE_OWN)
                        || botState1.equals(BotState.ACTIVE_SECOND_MODE_RAND)) {
                    sendMessage.setText(word.getRussianWordToString());
                    sendMessage.setReplyMarkup(buttons.createInlineHint(word.getEnglishWord()));
                    return sendMessage;
                }
                botState = null;
            }
            default -> {
                if(botStateCash.isInserting(chatId)) {
                    return insertHandler.insertHand(message,sendMessage);
                }
                else if(botStateCash.isRemoval(chatId)) {
                    if(inputMessage.equals("Удалить все")) {
                        return wordsTableService.deleteAll(chatId,sendMessage);
                    }
                    try {
                        int index = Integer.parseInt(inputMessage);
                        return wordsTableService.delete(index-1,chatId,sendMessage);
                    } catch (NumberFormatException e) {
                        sendMessage.setText("Ошибка "+Emojis.WRONG.getString()+", нужно ввести номер слова.");
                        return sendMessage;
                    }
                }
                else if(!botStateCash.isInserting(chatId) && botStateCash.getBotStateByChatId(chatId)!=null) {
                    botState = botStateCash.getBotStateByChatId(chatId);
                    return messageHandler.handle(message,botState);
                }
                else {
                    sendMessage.setText("Я не понимаю тебя!");
                }
                return sendMessage;
            }
        }
        return messageHandler.handle(message,botState);
    }
}
