package ru.spacelord.telegrambot.telegrambotwithspring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.spacelord.telegrambot.telegrambotwithspring.Entities.User;
import ru.spacelord.telegrambot.telegrambotwithspring.Entities.WordsTable;
import ru.spacelord.telegrambot.telegrambotwithspring.model.Emojis;
import ru.spacelord.telegrambot.telegrambotwithspring.model.parser.Word;
import ru.spacelord.telegrambot.telegrambotwithspring.reposiroty.UserRepository;
import ru.spacelord.telegrambot.telegrambotwithspring.reposiroty.WordsTableRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WordsTableService {
    private final WordsTableRepository wordsTableRepository;
    private final UserRepository userRepository;


    @Autowired
    public WordsTableService(WordsTableRepository wordsTableRepository,
                             UserRepository userRepository) {
        this.wordsTableRepository = wordsTableRepository;
        this.userRepository = userRepository;
    }

    public void save(WordsTable wordsTable) {
        wordsTableRepository.save(wordsTable);
    }

    @Transactional
    public SendMessage deleteAll(Long chatId, SendMessage sendMessage) {
        sendMessage.setText("Слова успешно удалены!"+ Emojis.CHECK.getString());
        wordsTableRepository.deleteAllByUser(userRepository.findByChatId(chatId));
        return sendMessage;
    }

    public SendMessage delete(int index, Long chatId, SendMessage sendMessage) {
        List<WordsTable> list = getAllByUser(chatId);
        if(list.size()<=index) {
            sendMessage.setText(String.format("Неверное значение."+Emojis.WRONG.getString()+ "У тебя всего %d слов",list.size()));
            return sendMessage;
        }
        WordsTable word = list.get(index);
        wordsTableRepository.delete(word);
        sendMessage.setText("Успешно удалил" + Emojis.CHECK.getString());
        return sendMessage;
    }

    public List<WordsTable> getAllByUser(Long chatId) {
        User user = userRepository.findByChatId(chatId);
        return wordsTableRepository.getAllByUser(user);
    }

    public List<Word> toListWord(List<WordsTable> list) {
        return list.stream().map(this::toWord).collect(Collectors.toList());
    }

    public String getAllUserWords(Long chatId) {
        StringBuilder builder = new StringBuilder();
        List<Word> list = toListWord(getAllByUser(chatId));
        for(Word word : list) {
            builder.append((list.indexOf(word)+1)).append(". ").append(word.getDoubleWords()).append("\n");
        }
        return builder.toString();
    }

    public Word toWord(WordsTable wordsTable) {
        return Word.builder()
                .russianWord(Arrays.stream(wordsTable.getRussianWord().split(",")).map(String::trim).collect(Collectors.toList()))
                .englishWord(wordsTable.getEnglishWord())
                .build();
    }
}
