package ru.disdev.network.pojo;

import java.io.UnsupportedEncodingException;

/**
 * Created by DisDev on 05.05.2016.
 */
public class MessagePacket {
    private final long userId;
    private final String message;
    private byte[] messageInByte = {};

    public MessagePacket(long userId, String message) {
        this.userId = userId;
        this.message = message;
        try {
            messageInByte = message.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {

        }
    }

    public long getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public byte[] getMessageInByte() {
        return messageInByte;
    }

    public int byteSize() {
        return Long.BYTES + messageInByte.length;
    }

    @Override
    public String toString() {
        return userId + ": " + message;
    }
}
