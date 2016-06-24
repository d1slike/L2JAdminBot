package ru.disdev.handler.impl;

import ru.disdev.handler.CommandArgs;
import ru.disdev.handler.Request;
import ru.disdev.network.GSCommunicator;

/**
 * Created by DisDev on 24.06.2016.
 */
@Request(command = "test", description = "Check current connection with game server")
public class Test extends AbstractRequest {
    @Override
    public String handle(int userId, CommandArgs args) {
        return GSCommunicator.getInstance().getStatus();
    }
}
