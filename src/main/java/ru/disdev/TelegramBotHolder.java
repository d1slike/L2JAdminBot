package ru.disdev;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;

/**
 * Created by Dislike on 03.05.2016.
 */
public class TelegramBotHolder {

    private static L2JAdminBot l2JAdminBot;

    public static void main(String... args) {
        TelegramBotsApi botsApi = new TelegramBotsApi();
        l2JAdminBot = new L2JAdminBot(Cfg.BOT_NAME, Cfg.TOKEN);
        try {
            botsApi.registerBot(l2JAdminBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static L2JAdminBot getL2JAdminBot() {
        return l2JAdminBot;
    }
}
