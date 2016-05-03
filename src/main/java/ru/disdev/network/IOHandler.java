package ru.disdev.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.disdev.TelegramBotHolder;

/**
 * Created by Dislike on 03.05.2016.
 */
public class IOHandler extends SimpleChannelInboundHandler<String> {

    private Channel activeChanel;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        TelegramBotHolder.getL2JAdminBot().sendMessage(s);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        activeChanel = ctx.channel();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    public void sendMessage(String message) {
        if (activeChanel != null)
            activeChanel.write(message);
    }
    
}
