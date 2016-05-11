package ru.disdev.handler.impl;

import ru.disdev.TelegramBotHolder;
import ru.disdev.handler.CommandArgs;
import ru.disdev.handler.Request;

/**
 * Created by DisDev on 11.05.2016.
 */
@Request(command = "mute", description = "Mute all notifications")
public class Mute extends AbstractRequest {
    @Override
    public String handle(long chatId, CommandArgs args) {
        boolean success = TelegramBotHolder.getL2JAdminBot().addChatToMuteList(chatId);
        return success ? "You was added to mute list." : "You are already in mute list.";
    }
}
