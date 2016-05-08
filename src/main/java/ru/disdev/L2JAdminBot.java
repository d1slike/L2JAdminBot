package ru.disdev;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import ru.disdev.network.GSCommunicator;
import ru.disdev.network.objects.MessagePacket;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Dislike on 03.05.2016.
 */
public class L2JAdminBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = LogManager.getLogger(L2JAdminBot.class);
    private static final int CHAT_ID_TO_ANNOUNCE_TO_ALL = -7;

    private final Set<Long> activeChats;
    private GSCommunicator communicator;

    public L2JAdminBot(final GSCommunicator communicator) {
        this.communicator = communicator;
        activeChats = new CopyOnWriteArraySet<>();
    }

    @Override
    public void onUpdateReceived(Update update) {

        Message message = update.getMessage();
        if (!message.hasText())
            return;
        final User user = message.getFrom();
        final int userId = user.getId();
        String log;
        if (accept(userId)) {
            activeChats.add(message.getChatId());
            String text = message.getText();
            log = String.format("Message(%s) received from user(%s %s) with id %d",
                    text,
                    user.getFirstName(),
                    user.getLastName(),
                    userId
            );
            if (text.startsWith("/")) {
                if (communicator != null)
                    communicator.sendMessageToGameServer(new MessagePacket(message.getChatId(), text.substring(1)));
            } else
                sendMessageToUser(message.getChatId(), "Command should start from '/'. Use /help to look to all commands.");
        } else {
            log = String.format("User(%d,%s,%s) with message(%s) try to communicate with bot but he is not in list.",
                    userId,
                    user.getFirstName(),
                    user.getLastName(),
                    message.getText());
            sendMessageToUser(message.getChatId(), "This bot is not for you. Goodbye!");
        }

        LOGGER.info(log);

    }

    @Override
    public String getBotUsername() {
        return Config.BOT_NAME;
    }

    public void sendMessageToUser(long chatId, String message) {
        if (chatId == CHAT_ID_TO_ANNOUNCE_TO_ALL)
            sendMessageToAllActiveUser(message);
        else
            send(chatId, message);
    }

    private boolean accept(int userId) {
        for (int i = 0; i < Config.USERS_IDS.length; i++)
            if (userId == Config.USERS_IDS[i])
                return true;
        return false;
    }

    @Override
    public String getBotToken() {
        return Config.TOKEN;
    }

    public void sendMessageToAllActiveUser(String message) {
        activeChats.forEach(chatId -> send(chatId, message));
    }

    private void send(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.enableMarkdown(true);
        sendMessage.setText(message);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            LOGGER.error("Error while sending message: " + message + ".", e);
        }
    }
}
