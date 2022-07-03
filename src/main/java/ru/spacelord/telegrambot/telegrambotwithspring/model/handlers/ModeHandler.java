package ru.spacelord.telegrambot.telegrambotwithspring.model.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.spacelord.telegrambot.telegrambotwithspring.model.BotState;
import ru.spacelord.telegrambot.telegrambotwithspring.model.BotStateCash;
import ru.spacelord.telegrambot.telegrambotwithspring.model.Buttons;
import ru.spacelord.telegrambot.telegrambotwithspring.model.Emojis;
import ru.spacelord.telegrambot.telegrambotwithspring.model.parser.Word;
import ru.spacelord.telegrambot.telegrambotwithspring.model.parser.WordsParser;
import ru.spacelord.telegrambot.telegrambotwithspring.services.UserService;
import ru.spacelord.telegrambot.telegrambotwithspring.services.WordsTableService;

import java.util.*;

@Component
public class ModeHandler {

    private final HashMap<Long, List<Word>> wordsMap = new HashMap<>();
    private final HashMap<Long,Word> wordNow = new HashMap<>();
    private final WordsParser wordsParser;
    private final UserService userService;
    private final Buttons buttons;
    private final WordsTableService wordsTableService;
    private final BotStateCash botStateCash;


    @Autowired
    public ModeHandler(WordsParser wordsParser,
                       UserService userService,
                       Buttons buttons,
                       WordsTableService wordsTableService,
                       BotStateCash botStateCash) {
        this.wordsParser = wordsParser;
        this.userService = userService;
        this.buttons = buttons;
        this.wordsTableService = wordsTableService;
        this.botStateCash = botStateCash;
    }

    public List<Word> createWords(Long chatId) {
        wordsMap.put(chatId, List.copyOf(wordsParser.getWords()));
        return wordsMap.get(chatId);
    }

    public String giveWordsForUser(Long chatId) {
        StringBuilder builder = new StringBuilder();
        List<Word> words = List.copyOf(createWords(chatId));
        for(Word word : words) {
            builder.append(word.getDoubleWords()).append("\n");
        }
        return builder.toString();
    }

    public String giveUserWordsForUser(Long chatId) {
        StringBuilder builder = new StringBuilder();
        List<Word> words = wordsTableService.toListWord(wordsTableService.getAllByUser(chatId));
        wordsMap.put(chatId,words);
        for(Word word : words) {
            builder.append(word.getDoubleWords()).append("\n");
        }
        return builder.toString();
    }

    public Word getWordFromMap(Long chatId) {
        List<Word> list = new ArrayList<>(List.copyOf(wordsMap.get(chatId)));
        Collections.shuffle(list);
        wordNow.put(chatId,list.get(0));
        return wordNow.get(chatId);
    }

    private void deleteWordFromMap(Long chatId) {
        List<Word> list = new ArrayList<>(List.copyOf(wordsMap.get(chatId)));
        list.remove(wordNow.get(chatId));
        wordsMap.put(chatId,list);
    }

    public BotApiMethod<?> checkFirstAnswer(Message message) {
        String answer = message.getText().toLowerCase(Locale.ROOT);
        Long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        if(wordNow.get(chatId).getRussianWord().contains(answer)) {
            if (isRightAnswer(chatId, sendMessage)) return sendMessage;
            sendMessage.setText("Верно!"+ Emojis.CHECK.getString() + "\n\n" + "Следующее слово: " + getWordFromMap(chatId).getEnglishWord());
            sendMessage.setReplyMarkup(buttons.createInlineHint(wordNow.get(chatId).getRussianWordToString()));
            return sendMessage;
        }
        sendMessage.setText("Неверно, попробуй еще раз!");
        return sendMessage;
    }

    public BotApiMethod<?> checkSecondAnswer(Message message) {
        String answer = message.getText().toLowerCase(Locale.ROOT);
        Long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        if(wordNow.get(chatId).getEnglishWord().equals(answer)) {
            if (isRightAnswer(chatId, sendMessage)) return sendMessage;
            sendMessage.setText("Верно!"+Emojis.CHECK.getString() + "\n\n" + "Следующее слово: " + getWordFromMap(chatId).getRussianWordToString());
            sendMessage.setReplyMarkup(buttons.createInlineHint(wordNow.get(chatId).getEnglishWord()));
            return sendMessage;
        }
        sendMessage.setText("Неверно,"+ Emojis.WRONG.getString() +"попробуй еще раз!");
        return sendMessage;
    }

    private boolean isRightAnswer(Long chatId, SendMessage sendMessage) {
        deleteWordFromMap(chatId);
        userService.givePoint(chatId);
        if(wordsMap.get(chatId).size()==0) {
            BotState botState = botStateCash.getBotStateByChatId(chatId);
            if(botState.equals(BotState.ACTIVE_FIRST_MODE_RAND) || botState.equals(BotState.ACTIVE_SECOND_MODE_RAND)) {
                sendMessage.setText("Верно! Держи следующий набор слов!" + "\n\n" + giveWordsForUser(chatId));
            } else if(botState.equals(BotState.ACTIVE_FIRST_MODE_OWN) || botState.equals(BotState.ACTIVE_SECOND_MODE_OWN)) {
                sendMessage.setText("Верно! Слова закончились, отправляю их второй раз. Ты можешь удалить или добавить новые в главном меню!" +
                        "\n" + giveUserWordsForUser(chatId));
            }
            return true;
        }
        return false;
    }

    public BotApiMethod<?> ownWordsCase(Long chatId, SendMessage sendMessage) {
        if(botStateCash.getBotStateByChatId(chatId)==null) {
            sendMessage.setText("Ошибка!" +Emojis.WRONG.getString()+"Попробуй сначала /start");
            return sendMessage;
        }
        if(wordsTableService.getAllByUser(chatId).size()==0) {
            sendMessage.setText("У вас еще нет слов! Ты можешь ввести их в главном меню " + Emojis.INSERT.getString());
            return sendMessage;
        }
        sendMessage.setText(botStateCash.getBotStateByChatId(chatId).getDescription() + "\n\n" + giveUserWordsForUser(chatId));
        if(botStateCash.getBotStateByChatId(chatId).equals(BotState.FIRST_MODE)) {
            botStateCash.saveBotState(chatId,BotState.ACTIVE_FIRST_MODE_OWN);
        }
        else if(botStateCash.getBotStateByChatId(chatId).equals(BotState.SECOND_MODE)) {
            botStateCash.saveBotState(chatId,BotState.ACTIVE_SECOND_MODE_OWN);
        }
        return buttons.createButtons(sendMessage,"Готов!","Выйти в главное меню");
    }

    public BotApiMethod<?> randomWordsCase(Long chatId, SendMessage sendMessage) {
        if(botStateCash.getBotStateByChatId(chatId)==null) {
            sendMessage.setText("Ошибка!" +Emojis.WRONG.getString()+"Попробуй сначала /start");
            return sendMessage;
        }
        sendMessage.setText(botStateCash.getBotStateByChatId(chatId).getDescription() + "\n\n" + giveWordsForUser(chatId));
        if(botStateCash.getBotStateByChatId(chatId).equals(BotState.FIRST_MODE)) {
            botStateCash.saveBotState(chatId,BotState.ACTIVE_FIRST_MODE_RAND);
        } else if(botStateCash.getBotStateByChatId(chatId).equals(BotState.SECOND_MODE)) {
            botStateCash.saveBotState(chatId,BotState.ACTIVE_SECOND_MODE_RAND);
        }
        return buttons.createButtons(sendMessage,"Готов!","Выйти в главное меню");
    }
}
