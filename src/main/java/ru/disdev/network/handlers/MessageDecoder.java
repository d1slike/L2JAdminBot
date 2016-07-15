package ru.disdev.network.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import ru.disdev.network.packets.MessagePacket;
import ru.disdev.network.packets.Packet;
import ru.disdev.network.packets.PacketsFactory;

import java.util.List;

/**
 * Created by DisDev on 05.05.2016.
 */

public class MessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        final byte key = in.readByte();
        Packet packet = PacketsFactory.create(key);
        if (packet == null)
            return;
        packet.setBuffer(in);
        packet.decode();
        packet.setBuffer(null);
        out.add(packet);
    }
}
