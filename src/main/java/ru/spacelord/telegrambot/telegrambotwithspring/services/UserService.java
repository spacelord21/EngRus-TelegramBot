package ru.spacelord.telegrambot.telegrambotwithspring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.spacelord.telegrambot.telegrambotwithspring.Entities.User;
import ru.spacelord.telegrambot.telegrambotwithspring.reposiroty.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService{

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void givePoint(Long chatId) {
        userRepository.givePoint(chatId);
    }

    public boolean isNotExist(Long chatId) {
        return !userRepository.existsByChatId(chatId);
    }

    public User getUserByChatId(Long chatId) {
        return userRepository.findByChatId(chatId);
    }

    public String topTen() {
        StringBuilder builder = new StringBuilder();
        builder.append("Топ-10 пользователей:").append("\n\n");
        List<User> users = userRepository.getAllByIdIsNotNull();
        users = users.stream().filter(user -> user.getPoints()>0).collect(Collectors.toList());
        if(users.size()==0) {
            return "Пока что тут пусто :(";
        }
        users.sort((o1, o2) -> o2.getPoints().intValue() - o1.getPoints().intValue());
        if(users.size()>9) {
            users = users.subList(0,9);
        }
        for(User user : users) {
            builder.append(users.indexOf(user)+1)
                    .append(". ")
                    .append(user.getName())
                    .append(" - ")
                    .append(user.getPoints()).append(" очка(ов)").append("\n");
        }
        return builder.toString();
    }

}
