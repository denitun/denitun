package com.example.demo.refactor;

import com.example.demo.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

//chtob zakomitet smog
@Component
@Slf4j
public class Bot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final Map<String, List<Event>> favorites = new ConcurrentHashMap<>();

    public Bot(BotConfig config) {
        this.config = config;
    }

    public void onUpdateReceived(Update update) {
        List<Event> events = createEvents();
        List<PartialBotApiMethod<?>> answers = update.getMessage() != null ? answerToMessage(update, events) : answerToCallback(update, events);

        for (PartialBotApiMethod<?> answer : answers) {
            try {
                if (answer instanceof SendPhoto) {
                    execute((SendPhoto) answer);
                } else if (answer instanceof SendMessage) {
                    execute((SendMessage) answer);
                }
            } catch (TelegramApiException e) {
                log.debug(e.toString());
            }
        }
    }

    private List<PartialBotApiMethod<?>> answerToMessage(Update update, List<Event> events) {
        String chatId = update.getMessage().getChatId().toString();
        SendMessage.SendMessageBuilder builder = SendMessage.builder();
        builder.chatId(chatId);
        String messageText = update.getMessage().getText();

        if (messageText.equals("/start")) {
            builder.text("Привет");
            builder.replyMarkup(getAlwaysOnScreenKB());
            return List.of(builder.build());
        }
        if (messageText.equals("Фильмы")) {
            List<PartialBotApiMethod<?>> messages = new ArrayList<>();
            for (int i = 0; i < Math.min(events.size(), 7); i++) {
                Event event = events.get(i);
                builder.text(event.getName());
                builder.replyMarkup(getEventInfoWithCallbacksKB(event.getId()));
                messages.add(builder.build());
            }
            return messages;
        }
        if (messageText.equals("Избранное")) {
            List<PartialBotApiMethod<?>> messages = new ArrayList<>();
            List<Event> favoriteEvents = favorites.get(chatId);
//            for (int i = 0; i < favoriteEvents.size(); i++) {
//                Event favoriteEvent = favoriteEvents.get(i);
//            } равно нижней строчке
            for (Event favoriteEvent : favoriteEvents) {
                builder.text(favoriteEvent.getName());
                messages.add(builder.build());
            }
            return messages;
        }
        if (messageText.equals("Игры")) {
            builder.text("Игры");
            return List.of(builder.build());
        }
        if (messageText.equals("Другое")) {
            builder.text("Другое");
            return List.of(builder.build());
        }
        if (messageText.equals("Все")) {
            builder.text("Все");
            return List.of(builder.build());
        }

        throw new RuntimeException("Unknown command");
    }

    private List<PartialBotApiMethod<?>> answerToCallback(Update update, List<Event> events) {
        String chatId = update.getCallbackQuery().getFrom().getId().toString();
        String callbackText = update.getCallbackQuery().getData();

        if (callbackText.startsWith("Подробнее")) {
            return List.of(getSendPhoto(events, chatId, callbackText));
        }
        if (callbackText.startsWith("Избранное")) {
            return List.of(getFavoritesAnswer(events, chatId, callbackText));
        }

        throw new RuntimeException("Unknown command");
    }

    private SendMessage getFavoritesAnswer(List<Event> events, String chatId, String callbackText) {
        SendMessage.SendMessageBuilder builder = SendMessage.builder();
        builder.chatId(chatId);
        builder.text("OK");
        Integer eventId = Integer.parseInt(callbackText.split(" ")[1]);
        Event event = findEventById(events, eventId);

        List<Event> favoriteEvents = favorites.getOrDefault(chatId, new ArrayList<>()); //favorites.containsKey(chatId) ? favorites.get(chatId) : new ArrayList<>();
        favoriteEvents.add(event);
        favorites.put(chatId, favoriteEvents);
        return builder.build();
    }

    private SendPhoto getSendPhoto(List<Event> events, String chatId, String callbackText) {
        SendPhoto.SendPhotoBuilder builder = SendPhoto.builder();
        builder.chatId(chatId);
        Integer eventId = Integer.parseInt(callbackText.split(" ")[1]);
        Event event = findEventById(events, eventId);
        builder.replyMarkup(getEventDetailedInfoWithCallbacksKB(eventId));
        builder.caption(event.getName());
//            InputFile photo = event.getPhotoPath();
        InputFile photo = new InputFile(new File("/Users/aitunin/Downloads/738A382C-44E8-4DCA-950B-F7FBAB320EF1.jpg")); //TODO
        builder.photo(photo);
        return builder.build();
    }

    private Event findEventById(List<Event> events, Integer id) {
        for (Event event : events) {
            if (Objects.equals(event.getId(), id)) {
                return event;
            }
        }
        throw new RuntimeException("Не нашли");
    }

    private ReplyKeyboardMarkup getAlwaysOnScreenKB() {
        // Создаем клавиатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Первая строчка клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Добавляем кнопки в первую строчку клавиатуры
        keyboardFirstRow.add("Фильмы");
        keyboardFirstRow.add("Игры");

        // Вторая строчка клавиатуры
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        // Добавляем кнопки во вторую строчку клавиатуры
        keyboardSecondRow.add("Другое");
        keyboardSecondRow.add("Всё");

        KeyboardRow keyboardThirdRow = new KeyboardRow();
        keyboardThirdRow.add("Избранное");

        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        keyboard.add(keyboardThirdRow);
        // и устанваливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    private InlineKeyboardMarkup getEventDetailedInfoWithCallbacksKB(Integer eventId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup().builder().build();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Добавить в избранное");
        inlineKeyboardButton.setCallbackData("Избранное " + eventId);

        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Уведомить");
        inlineKeyboardButton2.setCallbackData("Button \"Уведомить\" has been pressed");


        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton);
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(inlineKeyboardButton2);


        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);


        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getEventInfoWithCallbacksKB(Integer eventId) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Подробнее");
        inlineKeyboardButton.setCallbackData("Подробнее " + eventId);

        List<InlineKeyboardButton> keyboardButtonOnlyRow = new ArrayList<>();
        keyboardButtonOnlyRow.add(inlineKeyboardButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonOnlyRow);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    private List<Event> createEvents() {
        //
//        ArrayList<String> movies = new ArrayList<String>();
//        ArrayList<String> games = new ArrayList<String>();
//        ArrayList<String> other = new ArrayList<String>();
//
//        movies.add("2022");
//        movies.add("28 февраля Атака титанов 4 сезон 24 серия");
//        movies.add("2 марта Бэтмен");
//        movies.add("7 марта Атака титанов 4 сезон 25 серия");
//        movies.add("14 марта Атака титанов 4 сезон 26 серия");
//        movies.add("14 апреля Фантастические твари: Тайны Дамблдора");
//        movies.add("3 июня Пацаны 3 сезон");
//        movies.add("6 июля Доктор Стрендж: в мультивселенной безумия");
//        movies.add("8 июля Тор: Любовь и гром");
//        movies.add("2 сентября Властелин колец: Кольца власти");
//        movies.add("10 ноября Чёрная пантера 2");
//        movies.add("16 декабря Аватар 2");
//        movies.add("2023");
//        movies.add("23 мая Стражи галактики 3");
//        movies.add("28 июля Человек муравей и оса: Квантомания");
//        movies.add("20 октября Дюна 2");
//        movies.add("");
//        movies.add("");
//        movies.add("");
//        movies.add("кингсман 3");
//        movies.add("Ведьмак 3 сезон");
//        movies.add("Сериал Magic the gathering");
//        movies.add("Аркейн");
//        movies.add("аниме Nier automata");
//        movies.add("Дом дракона");
//        movies.add("Локи 2 сезон");
//        movies.add("Аватар 3");
//        movies.add("Принц драконов 4 сезон");
//
//
//        movies.add("");
//        movies.add("");
//        movies.add("");

        return List.of(
                new Film(1, "Стражи галактики1", LocalDate.of(2023, 5, 23)),
                new Film(2, "Стражи галактики2", LocalDate.of(2023, 5, 23)),
                new Film(3, "Стражи галактики3", LocalDate.of(2022, 11, 8)),
                new Film(4, "Стражи галактики4", LocalDate.of(2023, 5, 23)),
                new Film(5, "Стражи галактики5", LocalDate.of(2023, 5, 23)),
                new Film(6, "Стражи галактики6", LocalDate.of(2022, 11, 8)),
                new Film(7, "Стражи галактики7", LocalDate.of(2023, 5, 23)),
                new Film(8, "Стражи галактики8", LocalDate.of(2023, 5, 23)),
                new Film(9, "Стражи галактики9", LocalDate.of(2023, 5, 23)),
                new Film(10, "Стражи галактики10", LocalDate.of(2023, 5, 23)),
                new Film(11, "Стражи галактики11", LocalDate.of(2023, 5, 23)),
                new Film(12, "Стражи галактики12", LocalDate.of(2023, 5, 23)),
                new Film(13, "Стражи галактики13", LocalDate.of(2023, 5, 23)),
                new Film(14, "Стражи галактики14", LocalDate.of(2023, 5, 23)),
                new TvShow(15, "Ведьмак", LocalDate.of(2023, 1, 1), 3)
        );
    }

    @Scheduled(cron = "${notify.cron}")
    public void notifyUsers() {
        log.debug("notify");
        for (Map.Entry<String, List<Event>> entry : favorites.entrySet()) {
            String chatId = entry.getKey();
            List<Event> favoriteEvents = entry.getValue();
            List<String> todayReleasedNames = new ArrayList<>();
            for (Event favoriteEvent : favoriteEvents) {
                if (favoriteEvent.getReleaseDate().equals(LocalDate.now())) {
                    todayReleasedNames.add(favoriteEvent.getName());
                }
            }
            if (!todayReleasedNames.isEmpty()) {
                SendMessage.SendMessageBuilder builder = SendMessage.builder();
                builder.chatId(chatId);
                builder.text(String.join("; ", todayReleasedNames) + " выходит сегодня!");
                try {
                    execute(builder.build());
                } catch (TelegramApiException e) {
                    log.debug(e.toString());
                }
            }
        }
    }

    public String getBotUsername() {
        return config.getBotUserName();
    }

    public String getBotToken() {
        return config.getToken();
    }

}

