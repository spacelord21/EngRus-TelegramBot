package ru.spacelord.telegrambot.telegrambotwithspring.model;

import org.springframework.stereotype.Component;

import java.util.HashMap;


@Component
public class BotStateCash {
    private final HashMap<Long,BotState> botStateMap = new HashMap<>();

    public void saveBotState(Long chatId, BotState botState) {
        botStateMap.put(chatId,botState);
    }

    public BotState getBotStateByChatId(Long chatId) {
        return botStateMap.get(chatId);
    }

    public void delete(Long chatId) {
        botStateMap.remove(chatId);
    }

    public boolean isActiveFirstRand(Long chatId) {
        return botStateMap.get(chatId)==BotState.ACTIVE_FIRST_MODE_RAND;
    }

    public boolean isActiveSecondRand(Long chatId) {
        return botStateMap.get(chatId)==BotState.ACTIVE_SECOND_MODE_RAND;
    }

    public boolean isActiveFirstOwn(Long chatId) {
        return botStateMap.get(chatId)==BotState.ACTIVE_FIRST_MODE_OWN;
    }

    public boolean isActiveSecondOwn(Long chatId) {
        return botStateMap.get(chatId)==BotState.ACTIVE_SECOND_MODE_OWN;
    }

    public boolean isRemoval(Long chatId) {
        return botStateMap.get(chatId)==BotState.REMOVAL;
    }

    public boolean isInserting(Long chatId) {
        return botStateMap.get(chatId) == BotState.INSERTING;
    }
}
