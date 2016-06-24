package ru.disdev.handler.impl;


import ru.disdev.TelegramBotHolder;
import ru.disdev.handler.CommandArgs;
import ru.disdev.handler.CommandUserHelper;
import ru.disdev.handler.Request;
import ru.disdev.network.GSCommunicator;
import ru.disdev.network.objects.MessagePacket;

/**
 * Created by Dislike on 03.05.2016.
 */
@Request(command = "help", description = "Show all commands")
public class Help extends AbstractRequest {

    @Override
    public String handle(int userId, CommandArgs args) {
        //TelegramBotHolder.getGSCommunicator().sendMessageToGameServer(new MessagePacket(userId, "help"));
        GSCommunicator.getInstance().sendMessageToGameServer(new MessagePacket(userId, "help"));
        return CommandUserHelper.getInfo();
    }
}
