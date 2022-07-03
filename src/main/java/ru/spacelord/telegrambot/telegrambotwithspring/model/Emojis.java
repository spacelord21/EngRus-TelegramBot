package ru.spacelord.telegrambot.telegrambotwithspring.model;

import com.vdurmont.emoji.EmojiParser;

public enum Emojis {

    CHECK {
        @Override
        public String getString() {
            return EmojiParser.parseToUnicode(":white_check_mark:");
        }
    },
    RIGHT {
        @Override
        public String getString() {
            return EmojiParser.parseToUnicode(":arrow_right:");
        }
    },
    LEFT {
        @Override
        public String getString() {
            return EmojiParser.parseToUnicode(":arrow_left:");
        }
    },
    WRONG {
        @Override
        public String getString() {
            return EmojiParser.parseToUnicode(":negative_squared_cross_mark:");
        }
    },
    ENGLISH_FLAG {
        @Override
        public String getString() {
            return EmojiParser.parseToUnicode(":gb:");
        }
    },
    RUSSIAN_FLAG {
        @Override
        public String getString() {
            return EmojiParser.parseToUnicode(":ru:");
        }
    },
    INFORMATION {
        @Override
        public String getString() {
            return EmojiParser.parseToUnicode(":clipboard:");
        }
    },
    INSERT {
        @Override
        public String getString() {
            return EmojiParser.parseToUnicode(":pencil:");
        }
    },
    HELP {
        @Override
        public String getString() {
            return EmojiParser.parseToUnicode(":sos:");
        }
    },
    TOP {
        @Override
        public String getString() {
            return EmojiParser.parseToUnicode(":top:");
        }
    };
    public abstract String getString();
}
