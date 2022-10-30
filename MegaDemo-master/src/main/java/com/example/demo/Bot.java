package com.example.demo;

import com.sun.research.ws.wadl.Application;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//chtob zakomitet smog
@Component
@Slf4j
public class Bot extends TelegramLongPollingBot  {
    Map<String, List<String>> passportsAndNames = new HashMap<String, List<String>>();
    List<String> names = new ArrayList<String>();

    final
    BotConfig config;
    public Bot(BotConfig config){
        this.config = config;
    }
    public void onUpdateReceived(Update update) {



        update.getUpdateId();
        SendMessage.SendMessageBuilder builder = SendMessage.builder();
        String messageText;
        String chatId;

        //
        ArrayList<String> movies = new ArrayList<String>();
        ArrayList<String> games = new ArrayList<String>();
        ArrayList<String> other = new ArrayList<String>();

        movies.add("2022");
        movies.add("28 февраля Атака титанов 4 сезон 24 серия");
        movies.add("2 марта Бэтмен");
        movies.add("7 марта Атака титанов 4 сезон 25 серия");
        movies.add("14 марта Атака титанов 4 сезон 26 серия");
        movies.add("14 апреля Фантастические твари: Тайны Дамблдора");
        movies.add("3 июня Пацаны 3 сезон");
        movies.add("6 июля Доктор Стрендж: в мультивселенной безумия");
        movies.add("8 июля Тор: Любовь и гром");
        movies.add("2 сентября Властелин колец: Кольца власти");
        movies.add("10 ноября Чёрная пантера 2");
        movies.add("16 декабря Аватар 2");
        movies.add("2023");
        movies.add("23 мая Стражи галактики 3");
        movies.add("28 июля Человек муравей и оса: Квантомания");
        movies.add("20 октября Дюна 2");
        movies.add("");
        movies.add("");
        movies.add("");
        movies.add("кингсман 3");
        movies.add("Ведьмак 3 сезон");
        movies.add("Сериал Magic the gathering");
        movies.add("Аркейн");
        movies.add("аниме Nier automata");
        movies.add("Дом дракона");
        movies.add("Локи 2 сезон");
        movies.add("Аватар 3");
        movies.add("Принц драконов 4 сезон");


        movies.add("");
        movies.add("");
        movies.add("");
        //\
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup().builder().build();


        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Добавить в избранное");
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Уведомить");

        inlineKeyboardButton.setCallbackData("Button \"Добавить в избранное\" has been pressed");
        inlineKeyboardButton2.setCallbackData("Button \"Уведомить\" has been pressed");


        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton);
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(inlineKeyboardButton2);


        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);


        inlineKeyboardMarkup.setKeyboard(rowList);


        //return new SendMessage().setChatId(chatId).setMessageText("Пример").setReplyMarkup(inlineKeyboardMarkup);

        // Создаем клавиуатуру
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


        if (update.getMessage() != null) {
            chatId = update.getMessage().getChatId().toString();
            builder.chatId(chatId);
            messageText = update.getMessage().getText();
        } else {
            chatId = update.getCallbackQuery().getFrom().getId().toString();
            builder.chatId(chatId);
            messageText = update.getCallbackQuery().getMessage().getText();
        }
        if (messageText.contains("/start")) {
            builder.text("Привет");
            builder.replyMarkup(inlineKeyboardMarkup);
            builder.replyMarkup(replyKeyboardMarkup);
            try {
                execute(builder.build());
            } catch (TelegramApiException e) {
                log.debug(e.toString());
            }
        }
        if (messageText.contains("Фильмы")) {
            for (int i = 1; i < 8; i++) {
                builder.text(movies.get(i).toString());
                InlineKeyboardMarkup inlineKeyboardMarkup2 = clava(i);


                builder.replyMarkup(inlineKeyboardMarkup2);
                try {
                    execute(builder.build());
                } catch (TelegramApiException e) {
                    log.debug(e.toString());
                }
            }
        }
        if (messageText.contains("Игры")) {
            builder.text("Игры");
            builder.replyMarkup(inlineKeyboardMarkup);
            try {
                execute(builder.build());
            } catch (TelegramApiException e) {
                log.debug(e.toString());
            }
        }
        if (messageText.contains("Избранное")) {
            for (int i = 0; i <= passportsAndNames.get(chatId).size(); i++) {
                if (passportsAndNames.get(chatId).get(i) != null) {
                    builder.text(passportsAndNames.get(chatId).get(i).toString());
                    InlineKeyboardMarkup inlineKeyboardMarkup2 = clava(i);

                    builder.replyMarkup(inlineKeyboardMarkup2);
                    try {
                        execute(builder.build());
                    } catch (TelegramApiException e) {
                        log.debug(e.toString());
                    }
                }
            }
        }
        if (update.getCallbackQuery() != null) {
            boolean schetchik = true;
                for (int i = 0; i < 8; i++) {
                    if (update.getCallbackQuery().getData().equals("Button \"Добавить в избранное\" has been pressed") ) {
                        if (schetchik == false){
                            break;
                        }
                        if (messageText.equals(movies.get(i).toString()) ){
                            builder.text("Готово");
                            names.add(movies.get(i).toString());
                            passportsAndNames.put(chatId, names);

                            schetchik = false;
                            try {
                                execute(builder.build());
                            } catch (TelegramApiException e) {
                                log.debug(e.toString());
                            }
                        } else {
                            passportsAndNames.put(chatId, null);
                        }
                    } else {

                    if (i == Integer.parseInt(update.getCallbackQuery().getData())) {
                        builder.text(movies.get(i).toString());
                        builder.replyMarkup(inlineKeyboardMarkup);
                        SendPhoto.SendPhotoBuilder builderPhoto = SendPhoto.builder();
                        builderPhoto.photo(new InputFile(new File("C:\\Users\\denis\\Desktop\\Солнышко рик и морти.jpg")));
                        File f = new File("C:\\Users\\denis\\Desktop\\Солнышко рик и морти.jpg");
                        builderPhoto.chatId(chatId);
                        try {
                            execute(builderPhoto.build());
                        } catch (TelegramApiException e) {
                            log.debug(e.toString());
                        }
                        try {
                            execute(builder.build());
                        } catch (TelegramApiException e) {
                            log.debug(e.toString());
                        }
                    }
                }
            }
        }

        if (messageText.contains("Другое")) {
            builder.text("Другое");
            builder.replyMarkup(inlineKeyboardMarkup);
            try {
                execute(builder.build());
            } catch (TelegramApiException e) {
                log.debug(e.toString());
            }
        }
        if (messageText.contains("Всё")) {
            builder.text("Всё");
            builder.replyMarkup(inlineKeyboardMarkup);
            try {
                execute(builder.build());
            } catch (TelegramApiException e) {
                log.debug(e.toString());
            }
        }
        if (messageText.contains("chatId")) {
            builder.text("Id Канала : " + chatId);
            try {
                execute(builder.build());
            } catch (TelegramApiException e) {
                log.debug(e.toString());
            }
        }




    }

    public static InlineKeyboardMarkup clava (int i) {
        InlineKeyboardMarkup inlineKeyboardMarkup2 = new InlineKeyboardMarkup().builder().build();

        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        inlineKeyboardButton3.setText("Подробнее");

        String callBackNumber = Integer.toString(i);
        inlineKeyboardButton3.setCallbackData(callBackNumber);

        List<InlineKeyboardButton> keyboardButtonOnlyRow = new ArrayList<>();
        keyboardButtonOnlyRow.add(inlineKeyboardButton3);
        List<List<InlineKeyboardButton>> rowList2 = new ArrayList<>();
        rowList2.add(keyboardButtonOnlyRow);
        inlineKeyboardMarkup2.setKeyboard(rowList2);
        return inlineKeyboardMarkup2;
    }
    public String getBotUsername(){
        return config.getBotUserName();
    }
    public String getBotToken(){
        return config.getToken();
    }


}

