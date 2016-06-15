package ru.disdev.handler.impl;

import ru.disdev.TelegramBotHolder;
import ru.disdev.handler.CommandArgs;
import ru.disdev.handler.Request;

/**
 * Created by DisDev on 11.05.2016.
 */
@Request(command = "unmute", description = "Remove from mute list")
public class Unmute extends AbstractRequest {
    @Override
    public String handle(int userId, CommandArgs args) {
        boolean success = TelegramBotHolder.getL2JAdminBot().removeChatFromMuteList(userId);
        return success ? "You was successfully removed from mute list." : "You are not in mute list.";
    }
}
