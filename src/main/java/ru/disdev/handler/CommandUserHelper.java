package ru.disdev.handler;



import ru.disdev.handler.impl.AbstractRequest;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by Dislike on 03.05.2016.
 */
public class CommandUserHelper {
    private static String info = "";

    public static void buildInformation(Map<String, AbstractRequest> map) {
        final StringBuilder infoBuilder = new StringBuilder("Bot commands:\n");
        map.forEach((s, abstractRequest) -> {
            Request annotation = abstractRequest.getClass().getAnnotation(Request.class);
            String description = annotation.description();
            StringBuilder argInfoBuilder = new StringBuilder().append(" ");
            Stream.of(annotation.args()).forEach(s1 -> argInfoBuilder.append(s1).append(" "));
            String argInfo = argInfoBuilder.toString();
            infoBuilder.append("/").append(s)
                    .append(argInfo)
                    .append("- ")
                    .append(description)
                    .append("\n");
        });
        info = infoBuilder.toString();
    }

    public static String getInfo() {
        return info;
    }
}
