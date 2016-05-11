package ru.disdev;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import ru.disdev.network.GSCommunicator;

/**
 * Created by Dislike on 03.05.2016.
 */
public class TelegramBotHolder {

    private static L2JAdminBot l2JAdminBot;
    private static GSCommunicator communicator;

    public static void main(String... args) {
        Config.load();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        communicator = new GSCommunicator();
        l2JAdminBot = new L2JAdminBot(communicator);
        try {
            botsApi.registerBot(l2JAdminBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        communicator.start();
    }

    public static GSCommunicator getGSCommunicator() {
        return communicator;
    }

    public static L2JAdminBot getL2JAdminBot() {
        return l2JAdminBot;
    }
}
