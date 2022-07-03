package ru.spacelord.telegrambot.telegrambotwithspring.Entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WordsTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "users_chatId")
    @ManyToOne
    private User user;
    private String englishWord;
    private String russianWord;
}
