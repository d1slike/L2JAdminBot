package gs.side.request.impl;

import gs.side.request.CommandArgs;
import gs.side.request.Request;

import java.util.*;
import java.util.stream.Stream;

/**
 * Created by Dislike on 03.05.2016.
 */
public abstract class AbstractRequest {

    public final String execute(String fullCommand) {

        Request requestAnnotation = getClass().getAnnotation(Request.class);

        String commandName = requestAnnotation.command();
        String[] requestFormat = requestAnnotation.format();

        List<String> args = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(fullCommand.trim(), " ");
        tokenizer.nextToken();
        while(tokenizer.hasMoreTokens())
            args.add(tokenizer.nextToken());

        if (requestFormat.length != args.size()) {
            StringBuilder stringBuilder = new StringBuilder("Bad command. Please check format: /");
            stringBuilder.append(commandName).append(" ");
            Stream.of(requestFormat).forEach(s -> stringBuilder.append(s).append(" "));
            return stringBuilder.toString();
        }

        return handle(createCommandArgs(requestFormat, args));

    }


    private CommandArgs createCommandArgs(String[] requestFormat, List<String> args) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < requestFormat.length; i++)
            map.put(requestFormat[i], args.get(i));
        return new CommandArgs(map);
    }

    public abstract String handle(CommandArgs args);


}
