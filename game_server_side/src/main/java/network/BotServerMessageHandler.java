package network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import network.objects.MessagePacket;

/**
 * Created by Admin on 04.05.2016.
 */
public class BotServerMessageHandler extends SimpleChannelInboundHandler<MessagePacket> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessagePacket s) throws Exception {
        System.err.println(s);
    }
}
