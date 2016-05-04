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

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Dislike on 03.05.2016.
 */
public class L2JAdminBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = LogManager.getLogger(L2JAdminBot.class);

    private final AtomicLong lastMessageChatId;
    private GSCommunicator communicator;

    public L2JAdminBot(final GSCommunicator communicator) {
        lastMessageChatId = new AtomicLong(-1);
        this.communicator = communicator;
    }


    @Override
    public void onUpdateReceived(Update update) {

        Message message = update.getMessage();
        if (message != null && message.hasText() && message.getText().startsWith("/")) {
            if (!message.getFrom().getFirstName().startsWith("Ян"))
                return;
            lastMessageChatId.set(message.getChatId());
            String command = message.getText().substring(1);
            User user = message.getFrom();
            LOGGER.info(String.format("Message(%s) received from user(%s %s) with id %s",
                    message.getText(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getId()
                    ));

            if (communicator != null)
                communicator.sendMessageToGameServer(command);
            /*RequestHolder.getInstance().get(command).ifPresent(abstractRequest -> {
                String result = abstractRequest.execute(command);
                sendMessage(message.getChatId(), result);
            });*/
        }
    }

    public void sendMessage(String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(lastMessageChatId.get() + "");
        sendMessage.enableMarkdown(true);
        sendMessage.setText(message);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return Cfg.BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return Cfg.TOKEN;
    }
}
