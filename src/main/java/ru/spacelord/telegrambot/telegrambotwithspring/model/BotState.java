package ru.spacelord.telegrambot.telegrambotwithspring.model;

public enum BotState {
    START{
        @Override
        public String getDescription() {
            return "Привет, воспользуйся главным меню!";
        }
    },INFORMATION{
        @Override
        public String getDescription() {
            return "Бот создан преимущественно в учебно-практических целях изучения языка программирования Java. " +
                    "В этих словах могут быть опечатки, неточности и т.д. При возможности буду чистить их и улучшать. " +
                    "Удачи! \n" +
                    "Слова взяты с сайта: https://studynow.ru/dicta/allwords";
        }
    },HELP{
        @Override
        public String getDescription() {
            return "Если есть проблемы или предложения пишите - @spacelord_21";
        }
    },FIRST_MODE{
        @Override
        public String getDescription() {
            return "Держи слова! Нажми Готов и начнем!";
        }
    },SECOND_MODE{
        @Override
        public String getDescription() {
            return "Держи слова! Нажми Готов и начнем!";
        }
    },ACTIVE_FIRST_MODE_RAND{
        @Override
        public String getDescription() {
            return "ACTIVE FIRST RAND";
        }
    },ACTIVE_SECOND_MODE_RAND{
        @Override
        public String getDescription() {
            return "ACTIVE SECOND RAND";
        }

    },ACTIVE_FIRST_MODE_OWN{
        @Override
        public String getDescription() {
            return "ACTIVE FIRST OWN";
        }
    },ACTIVE_SECOND_MODE_OWN{
        @Override
        public String getDescription() {
            return "ACTIVE SECOND OWN";
        }
    }
    ,INSERTING{
        @Override
        public String getDescription() { return """
                Запись слова происходит по формату: английское слово - русское/русские слова.

                Примеры: car - машина; retire - уйти в отставку, уйти на пенсию""";}
    },REMOVAL{
        @Override
        public String getDescription() {
            return "Для удаления введи цифру, соответствующую слову. Если ты хочешь удалить все слова сразу, то нажми на " +
                    "кнопку Удалить все";
        }
    };

    public abstract String getDescription();

}
