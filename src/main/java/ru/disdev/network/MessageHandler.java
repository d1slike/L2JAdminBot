package ru.disdev.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.disdev.TelegramBotHolder;
import ru.disdev.network.packets.ChatMessagePacket;
import ru.disdev.network.packets.MessagePacket;
import ru.disdev.network.packets.Packet;

import javax.net.ssl.SSLException;
import java.io.IOException;


/**
 * Created by DisDev on 04.05.2016.
 */
@ChannelHandler.Sharable
public class MessageHandler extends SimpleChannelInboundHandler<Packet> {

    private static final Logger LOGGER = LogManager.getLogger(MessageHandler.class);


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (GSCommunicator.getInstance().hasActiveChannel()) {
            LOGGER.warn(ctx.channel().localAddress() + " trying to connect. But handler already has active channel " + GSCommunicator.getInstance().getActiveChannel().localAddress());
            ctx.close();
            return;
        }
        String adress = ctx.channel().localAddress().toString();
        /*if (!adress.equals(Config.GS_ADDRESS)) {
            LOGGER.warn(adress + " try to connect to bot server. Connection drop.");
            ctx.close();
            return;
        }*/

        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
                (GenericFutureListener<Future<Channel>>) future -> {
                    if (future.isSuccess()) {
                        //activeChannel = future.get();
                        GSCommunicator.getInstance().setActiveChannel(future.get());
                        LOGGER.info("Successfully connected to " + adress);
                    } else
                        ctx.close();

                });

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        final boolean isActiveChannel = resetActiveChannelIfEqualWith(ctx.channel());

        if (cause instanceof SSLException) {
            LOGGER.warn("Insecure connection: " + ctx.channel().localAddress());
        } else if (cause instanceof IOException) {
            LOGGER.warn("Server was shutdown abnormally: " + ctx.channel().localAddress());
            if (isActiveChannel)
                TelegramBotHolder.getL2JAdminBot().sendMessageToAllActiveUser("Warn! Server was shutdown abnormally.");
        } else {
            LOGGER.error("Critical error! Connection will close.", cause);
        }

        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (resetActiveChannelIfEqualWith(ctx.channel())) {
            TelegramBotHolder.getL2JAdminBot().sendMessageToAllActiveUser("Warn! Server was shutdown.");
            LOGGER.warn("Disconnection with gs: " + ctx.channel().localAddress());
        }
    }

    private boolean resetActiveChannelIfEqualWith(Channel channel) {
        final GSCommunicator communicator = GSCommunicator.getInstance();
        boolean equal = communicator.hasActiveChannel() && communicator.getActiveChannel().equals(channel);
        if (equal)
            communicator.setActiveChannel(null);
        return equal;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
        boolean needLog = false;
        switch (msg.key()) {
            case MessagePacket.KEY: {
                MessagePacket packet = (MessagePacket) msg;
                needLog = true;
                TelegramBotHolder.getL2JAdminBot().sendMessageToUserById(packet.getUserId(), packet.getMessage());
                break;
            }
            case ChatMessagePacket.KEY: {
                ChatMessagePacket packet = (ChatMessagePacket) msg;
                TelegramBotHolder.getL2JAdminBot().sendGameChatMessage(packet);
                break;
            }
        }
        if (needLog)
            LOGGER.info(String.format("Packet received from %s, (%s)", ctx.channel().localAddress(), msg));

    }
}
