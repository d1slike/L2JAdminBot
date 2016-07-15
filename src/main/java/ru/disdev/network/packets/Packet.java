package ru.disdev.network.packets;

import io.netty.buffer.ByteBuf;

/**
 * Created by Dislike on 15.07.2016.
 */
public abstract class Packet {
    private ByteBuf buffer;

    public final ByteBuf getBuffer() {
        return buffer;
    }

    public final void setBuffer(ByteBuf buffer) {
        this.buffer = buffer;
    }

    void writeInt(int value) {
        buffer.writeInt(value);
    }

    void writeString(String value) {
        for (char c : value.toCharArray())
            buffer.writeChar(c);
        buffer.writeChar('\0');
    }

    int readInt() {
        return buffer.readInt();
    }

    String readString() {
        StringBuilder builder = new StringBuilder();
        char c;
        while (buffer.isReadable() && (c = buffer.readChar()) != '\0')
            builder.append(c);
        return builder.toString();
    }

    public abstract void encode();
    public abstract void decode();
    public abstract byte key();
}
