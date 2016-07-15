package ru.disdev.handler.impl;

import ru.disdev.L2JAdminBot;
import ru.disdev.TelegramBotHolder;
import ru.disdev.handler.CommandArgs;
import ru.disdev.handler.Request;

/**
 * Created by Dislike on 15.07.2016.
 */
@Request(command = "gamechat", description = "subscribe/unsubscribe from game chat stream")
public class GameChat extends AbstractRequest {
    @Override
    public String handle(int userId, CommandArgs args) {
        L2JAdminBot bot = TelegramBotHolder.getL2JAdminBot();
        if (bot.addGameChatSubscriber(userId))
            return "Subscribed!";
        else if (bot.removeGameChatSubscriber(userId))
            return "unsubscribed!";
        return "Nothing to do!";
    }
}
