package ru.spacelord.telegrambot.telegrambotwithspring.model.parser;


import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


@Component
public class WordsParser {

    private final List<Word> allWords = new ArrayList<>();


    public WordsParser(){
        try {
            Scanner scanner = new Scanner(new File("src/main/resources/words.csv"));
            while (scanner.hasNext()) {
                allWords.add(parse(scanner.nextLine()));
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<Word> getWords() {
        int max = (int)(Math.random()*4985)+10;
        return allWords.subList(max-10,max);
    }

    private Word parse(String line) {
        Scanner scanner = new Scanner(line);
        scanner.useDelimiter(";");
        String enWord = scanner.next();
        List<String> rusWords = new ArrayList<>();
        String newLine = scanner.next();
        scanner.close();
        Scanner scanner1 = new Scanner(newLine);
        scanner1.useDelimiter(",");
        while(scanner1.hasNext()) {
            rusWords.add(scanner1.next().trim());
        }
        scanner1.close();
        return new Word(enWord,rusWords);
    }
}
