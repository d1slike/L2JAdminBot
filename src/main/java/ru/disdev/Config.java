package ru.disdev;
import jfork.nproperty.Cfg;
import jfork.nproperty.ConfigParser;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Dislike on 03.05.2016.
 */
public class Config {
    @Cfg("BotName")
    public static String BOT_NAME = "";
    @Cfg("BotToken")
    public static String TOKEN = "";
    @Cfg("BotServerPort")
    public static int BOT_PORT = 9191;
    @Cfg("BotServerHost")
    public static String BOT_HOST = "127.0.0.1";
    @Cfg(value = "AllowedUsersList", splitter = ",")
    public static int[] USERS_IDS = {};

    static void load() {
        try {
            ConfigParser.parse(Config.class, "config/config.properties");
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException | IOException e) {
            e.printStackTrace();
        }
    }

    static void reload() {
        load();
    }
}
