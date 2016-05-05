package ru.disdev.network.objects;

import java.io.UnsupportedEncodingException;

/**
 * Created by DisDev on 05.05.2016.
 */
public class MessagePacket {
    private final long chatId;
    private final String message;
    private byte[] messageInByte = {};

    public MessagePacket(long chatId, String message) {
        this.chatId = chatId;
        this.message = message;
        try {
            messageInByte = message.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {

        }
    }

    public long getChatId() {
        return chatId;
    }

    public String getMessage() {
        return message;
    }

    public byte[] getMessageInByte() {
        return messageInByte;
    }

    @Override
    public String toString() {
        return chatId + ": " + message;
    }
}
