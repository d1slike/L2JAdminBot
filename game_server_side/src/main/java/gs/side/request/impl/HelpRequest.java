package gs.side.request.impl;

import gs.side.CommandUserHelper;
import gs.side.request.CommandArgs;
import gs.side.request.Request;

/**
 * Created by Dislike on 03.05.2016.
 */
@Request(command = "help")
public class HelpRequest extends AbstractRequest {
    @Override
    public String handle(CommandArgs args) {
        return CommandUserHelper.getInfo();
    }
}
