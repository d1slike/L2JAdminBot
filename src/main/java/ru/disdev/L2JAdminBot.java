package ru.disdev;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import ru.disdev.handler.RequestHolder;
import ru.disdev.handler.impl.AbstractRequest;
import ru.disdev.network.GSCommunicator;
import ru.disdev.network.packets.ChatMessagePacket;
import ru.disdev.network.packets.MessagePacket;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Dislike on 03.05.2016.
 */
public class L2JAdminBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = LogManager.getLogger(L2JAdminBot.class);
    private static final int USER_ID_TO_ANNOUNCE_TO_ALL = -7;

    private final Map<Integer, Long> activeChats;
    private final Set<Long> muteChatList;
    private final Set<Long> gameChatSubscribers;

    public L2JAdminBot() {
        activeChats = new ConcurrentHashMap<>();
        muteChatList = new CopyOnWriteArraySet<>();
        gameChatSubscribers = new CopyOnWriteArraySet<>();
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
            final long chatId = message.getChatId();
            activeChats.put(userId, chatId);
            final String text = message.getText();
            log = String.format("Message(%s) received from user(%s %s) with id %d",
                    text,
                    user.getFirstName(),
                    user.getLastName(),
                    userId
            );
            String answer = handleMessage(userId, text);
            if (answer != null)
                sendMessageToUserById(userId, answer);
        } else {
            log = String.format("User(%d,%s,%s) with message(%s) try to communicate with bot but he is not in list.",
                    userId,
                    user.getFirstName(),
                    user.getLastName(),
                    message.getText());
            //sendMessageToUserById(message., "This bot is not for you. Goodbye!");
        }

        LOGGER.info(log);

    }

    private String handleMessage(int userId, String message) {
        if (message.startsWith("/")) {
            final String command = message.substring(1);
            Optional<AbstractRequest> request = RequestHolder.getInstance().get(command);
            if (request.isPresent())
                return request.get().execute(userId, command);
            else {
                /*if (TelegramBotHolder.getGSCommunicator() != null)
                    TelegramBotHolder.getGSCommunicator().sendMessageToGameServer(new MessagePacket(userId, command));*/
                GSCommunicator.getInstance().sendMessageToGameServer(new MessagePacket(userId, command));
                return null;
            }
        } else
            return "Command should start from '/'. Use /help to look to all commands.";
    }

    @Override
    public String getBotUsername() {
        return Config.BOT_NAME;
    }

    public void sendMessageToUserById(int userId, String message) {
        if (userId == USER_ID_TO_ANNOUNCE_TO_ALL)
            sendMessageToAllActiveUser(message);
        else
            send(activeChats.get(userId), message);
    }

    private boolean accept(int userId) {
        for (int i = 0; i < Config.USERS_IDS.length; i++)
            if (userId == Config.USERS_IDS[i])
                return true;
        return false;
    }

    public boolean addChatToMuteList(int userId) {
        return muteChatList.add(activeChats.get(userId));
    }

    public boolean removeChatFromMuteList(int userId) {
        return muteChatList.remove(activeChats.get(userId));
    }

    public boolean addGameChatSubscriber(int userId) {
        return gameChatSubscribers.add(activeChats.get(userId));
    }

    public boolean removeGameChatSubscriber(int userId) {
        return gameChatSubscribers.remove(activeChats.get(userId));
    }

    public void sendGameChatMessage(ChatMessagePacket packet) {
        final String message = packet.toString();
        gameChatSubscribers.forEach(chatId -> send(chatId, message));
    }

    @Override
    public String getBotToken() {
        return Config.TOKEN;
    }

    public void sendMessageToAllActiveUser(String message) {
        //activeChats.stream().filter(chatId -> !muteChatList.contains(chatId)).forEach(chatId -> send(chatId, message));
        activeChats.forEach((userId, chatId) -> {
            if (!muteChatList.contains(chatId))
                send(chatId, message);
        });
    }

    private void send(Long chatId, String message) {
        if (!activeChats.containsValue(chatId))
            return;
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
