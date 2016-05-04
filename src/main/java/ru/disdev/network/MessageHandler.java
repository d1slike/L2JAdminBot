package ru.disdev.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Created by DisDev on 04.05.2016.
 */
public class MessageHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger LOGGER = LogManager.getLogger(MessageHandler.class);

    private Channel activeChannel;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String message) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (activeChannel != null) {
            LOGGER.warn(ctx.channel().remoteAddress() + " trying to connect. But handler already has active channel " + activeChannel.localAddress());
            return;
        }
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
                (GenericFutureListener<Future<Channel>>) future -> {
                    activeChannel = future.getNow();
                    LOGGER.info("Successfully connected to " + activeChannel.remoteAddress());
                });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("Critical error!. Connection will close!", cause);
        activeChannel = null;
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.warn("Game server " + ctx.channel().remoteAddress() + " was shutdown.");
        activeChannel = null;
    }

    public void writeMessage(String message) {
        if (activeChannel != null)
            activeChannel.writeAndFlush(message);
    }


}
