package ru.disdev;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import ru.disdev.network.Server;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Dislike on 03.05.2016.
 */
public class L2JAdminBot extends TelegramLongPollingBot {


    private final String botToken;
    private final String botUsername;
    private final AtomicLong lastMessageChatId;
    private Server server = null;

    public L2JAdminBot(final String botToken, final String botUsername) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        lastMessageChatId = new AtomicLong(-1);
        try {
            server = new Server();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onUpdateReceived(Update update) {

        Message message = update.getMessage();
        if (message != null && message.hasText() && message.getText().startsWith("/")) {
            if (!message.getFrom().getFirstName().startsWith("Ян"))
                return;
            String command = message.getText().substring(1);
            if (server != null)
                server.sendMessageToGameServer(command);
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
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
