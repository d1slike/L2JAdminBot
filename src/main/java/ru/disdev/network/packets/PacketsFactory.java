package ru.disdev.network.packets;

/**
 * Created by Dislike on 15.07.2016.
 */
public class PacketsFactory {
    public static Packet create(final byte key) {
        switch (key) {
            case ChatMessagePacket.KEY:
                return new ChatMessagePacket();
            case MessagePacket.KEY:
                return new MessagePacket();
        }
        return null;
    }
}
