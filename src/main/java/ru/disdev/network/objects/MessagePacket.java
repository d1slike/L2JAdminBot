package ru.disdev.network.objects;

import java.io.UnsupportedEncodingException;

/**
 * Created by DisDev on 05.05.2016.
 */
public class MessagePacket {
    private final int userId;
    private final String message;
    private byte[] messageInByte = {};

    public MessagePacket(int userId, String message) {
        this.userId = userId;
        this.message = message;
        try {
            messageInByte = message.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {

        }
    }

    public int getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public byte[] getMessageInByte() {
        return messageInByte;
    }

    @Override
    public String toString() {
        return userId + ": " + message;
    }
}
