package ru.disdev;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import ru.disdev.network.GSCommunicator;

/**
 * Created by Dislike on 03.05.2016.
 */
public class TelegramBotHolder {

    private static L2JAdminBot l2JAdminBot;

    public static void main(String... args) {
        TelegramBotsApi botsApi = new TelegramBotsApi();
        GSCommunicator communicator = new GSCommunicator();
        l2JAdminBot = new L2JAdminBot(Cfg.BOT_NAME, Cfg.TOKEN, communicator);
        try {
            botsApi.registerBot(l2JAdminBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        communicator.start();
    }

    public static L2JAdminBot getL2JAdminBot() {
        return l2JAdminBot;
    }
}
