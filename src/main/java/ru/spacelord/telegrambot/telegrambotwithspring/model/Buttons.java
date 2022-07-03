package ru.spacelord.telegrambot.telegrambotwithspring.model;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;


import java.util.ArrayList;
import java.util.List;

@Component
public class Buttons {

    public SendMessage createButtons(SendMessage sendMessage, String...text) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setSelective(true);
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardRowFirst = new KeyboardRow();
        if(text.length==1) {
            keyboardRowFirst.add(new KeyboardButton(text[0]));
            keyboardRowList.add(keyboardRowFirst);
            replyKeyboardMarkup.setKeyboard(keyboardRowList);
            return sendMessage;
        }
        for (int i = 0; i < 2; i++) {
            keyboardRowFirst.add(new KeyboardButton(text[i]));
        }
        keyboardRowList.add(keyboardRowFirst);
        if (text.length >= 3) {
            KeyboardRow keyboardRowSecond = new KeyboardRow();
            for (int i = 2; i < text.length; i++) {
                keyboardRowSecond.add(new KeyboardButton(text[i]));
            }
            keyboardRowList.add(keyboardRowSecond);
        }
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
        return sendMessage;
    }

    public InlineKeyboardMarkup createInlineHint(String rightAnswer) {
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        List<InlineKeyboardButton> firstRowButton = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Напомнить");
        button.setCallbackData(rightAnswer);
        firstRowButton.add(button);
        inlineButtons.add(firstRowButton);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(inlineButtons);
        return inlineKeyboardMarkup;
    }
}
