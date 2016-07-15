package ru.disdev.network.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import ru.disdev.network.packets.Packet;

/**
 * Created by DisDev on 05.05.2016.
 */
@ChannelHandler.Sharable
public class MessageEncoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
        msg.setBuffer(out);
        out.writeByte(msg.key());
        msg.encode();
        msg.setBuffer(null);
    }
}
