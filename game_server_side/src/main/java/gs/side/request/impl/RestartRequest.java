package gs.side.request.impl;

import gs.side.request.CommandArgs;
import gs.side.request.Request;

/**
 * Created by Dislike on 03.05.2016.
 */
@Request(command = "restart", format = "delayInSec", description = "Планирует перезагрузку сервера с указанной задержкой")
public class RestartRequest extends AbstractRequest {
    @Override
    public String handle(CommandArgs args) {
        int delay = args.getInt("delayInSec");
        //Shutdown.getInstance().schedule(delay, Shutdown.RESTART);
        return "Ok";
    }
}
