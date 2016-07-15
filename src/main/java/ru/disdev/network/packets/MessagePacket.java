package ru.disdev.network.packets;

/**
 * Created by DisDev on 05.05.2016.
 */
public class MessagePacket extends Packet {

    public static final byte KEY = 1;

    private int userId;
    private String message;

    public MessagePacket(int userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public MessagePacket() {

    }

    public int getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return userId + ": " + message;
    }

    @Override
    public void encode() {
        writeInt(userId);
        writeString(message);
    }

    @Override
    public void decode() {
        userId = readInt();
        message = readString();
    }

    @Override
    public byte key() {
        return KEY;
    }
}
