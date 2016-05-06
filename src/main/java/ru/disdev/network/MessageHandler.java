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
import ru.disdev.network.objects.MessagePacket;


/**
 * Created by DisDev on 04.05.2016.
 */
@ChannelHandler.Sharable
public class MessageHandler extends SimpleChannelInboundHandler<MessagePacket> {

    private static final Logger LOGGER = LogManager.getLogger(MessageHandler.class);

    private Channel activeChannel;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessagePacket packet) throws Exception {
        TelegramBotHolder.getL2JAdminBot().sendMessageToUser(packet.getChatId(), packet.getMessage());
        LOGGER.info(String.format("Message(%s) received from gs(%s)", packet, channelHandlerContext.channel().localAddress()));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (activeChannel != null) {
            LOGGER.warn(ctx.channel().localAddress() + " trying to connect. But handler already has active channel " + activeChannel.localAddress());
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
                    activeChannel = future.get();
                    LOGGER.info("Successfully connected to " + adress);
                });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause.getLocalizedMessage().equals("Удаленный хост принудительно разорвал существующее подключение"))
            return;
        LOGGER.error("Critical error!. Connection will close!", cause);
        activeChannel = null;
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.warn("Game server " + ctx.channel().localAddress() + " was shutdown.");
        TelegramBotHolder.getL2JAdminBot().sendMessageToAllActiveUser("Warning! Game server was shutdown!");
        activeChannel = null;
    }

    public void writeMessage(MessagePacket messagePacket) {
        if (activeChannel != null)
            activeChannel.writeAndFlush(messagePacket);
    }


}
