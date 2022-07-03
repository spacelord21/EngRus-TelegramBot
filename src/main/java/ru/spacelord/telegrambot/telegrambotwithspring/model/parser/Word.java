package ru.spacelord.telegrambot.telegrambotwithspring.model.parser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class Word {
    private final String englishWord;
    private final List<String> russianWord;

    public String getDoubleWords() {
        StringBuilder builder = new StringBuilder();
        builder.append(englishWord).append(" - ").append(russianWord.get(0));
        if(russianWord.size()>1) {
            for(int i = 1; i < russianWord.size();i++) {
                builder.append(", ").append(russianWord.get(i));
            }
        }
        return builder.toString();
    }

    public String getRussianWordToString() {
        return String.join(",",russianWord);
    }
}
