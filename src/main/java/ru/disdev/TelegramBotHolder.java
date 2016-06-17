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
        Config.load();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        GSCommunicator.getInstance();
        l2JAdminBot = new L2JAdminBot();
        try {
            botsApi.registerBot(l2JAdminBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static GSCommunicator getGSCommunicator() {
        return GSCommunicator.getInstance();
    }

    public static L2JAdminBot getL2JAdminBot() {
        return l2JAdminBot;
    }
}
