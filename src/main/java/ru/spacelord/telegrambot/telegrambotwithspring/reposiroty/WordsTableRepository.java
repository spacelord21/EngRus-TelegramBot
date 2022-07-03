package ru.spacelord.telegrambot.telegrambotwithspring.reposiroty;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.spacelord.telegrambot.telegrambotwithspring.Entities.User;
import ru.spacelord.telegrambot.telegrambotwithspring.Entities.WordsTable;

import java.util.List;

public interface WordsTableRepository extends JpaRepository<WordsTable,Long> {
    List<WordsTable> getAllByUser(User user);
    void deleteAllByUser(User user);
}
