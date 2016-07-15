package ru.disdev.network.packets;

/**
 * Created by Dislike on 15.07.2016.
 */
public class ChatMessagePacket extends Packet {

    public static final byte KEY = 0;

    private String playerName;
    private String chatType;
    private String message;

    public ChatMessagePacket(String playerName, String chatType, String message) {
        this.playerName = playerName;
        this.chatType = chatType;
        this.message = message;
    }

    public ChatMessagePacket() {

    }

    @Override
    public void encode() {
        writeString(playerName);
        writeString(chatType);
        writeString(message);
    }

    @Override
    public void decode() {
        playerName = readString();
        chatType = readString();
        message = readString();
    }

    @Override
    public byte key() {
        return KEY;
    }

    @Override
    public String toString() {
        return String.format("%s:%s: %s", chatType, playerName, message);
    }

    public String getMessage() {
        return message;
    }

    public String getChatType() {
        return chatType;
    }

    public String getPlayerName() {
        return playerName;
    }
}
