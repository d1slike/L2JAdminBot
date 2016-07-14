package ru.disdev.network.objects;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by DisDev on 05.05.2016.
 */
@ChannelHandler.Sharable
public class MessageEncoder extends MessageToByteEncoder<MessagePacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessagePacket msg, ByteBuf out) throws Exception {
        out.writeInt(msg.getUserId()).writeBytes(msg.getMessageInByte());
    }
}
