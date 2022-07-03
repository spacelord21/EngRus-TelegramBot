package ru.spacelord.telegrambot.telegrambotwithspring.reposiroty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.spacelord.telegrambot.telegrambotwithspring.Entities.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByChatId(long chatId);
    List<User> getAllByIdIsNotNull();
    boolean existsByChatId(long chatId);

    @Transactional
    @Modifying
    @Query("update User set points=points+1 where chatId=:chatId")
    void givePoint(long chatId);

}
