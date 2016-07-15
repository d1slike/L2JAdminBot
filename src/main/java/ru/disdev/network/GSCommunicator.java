package ru.disdev.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.disdev.Config;
import ru.disdev.network.handlers.MessageDecoder;
import ru.disdev.network.handlers.MessageEncoder;
import ru.disdev.network.packets.MessagePacket;

import java.io.File;


/**
 * Created by DisDev on 04.05.2016.
 */
public class GSCommunicator {

    private static final Logger LOGGER = LogManager.getLogger(GSCommunicator.class);

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private Channel activeChannel;

    private GSCommunicator() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            SslContext sslContext = SslContextBuilder
                    .forClient()
                    .keyManager(new File("ssl/cert.pem"), new File("ssl/key_1.pem"))
                    .trustManager(new File("ssl/cert.pem")).build();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline channelPipeline = socketChannel.pipeline();
                            SslHandler handler = sslContext.newHandler(socketChannel.alloc());
                            channelPipeline.addLast(handler);
                            channelPipeline.addLast(new MessageDecoder(), new MessageEncoder(), new MessageHandler());
                        }
                    });

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        serverBootstrap.bind(Config.BOT_PORT).addListener(future -> {
            if (future.cause() == null)
                LOGGER.info("BotServer is up. Listening gameserver...");
            else
                LOGGER.error("Error while binding bot server", future.cause());
        });
    }

    public void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public void sendMessageToGameServer(MessagePacket messagePacket) {
        if (!hasActiveChannel()) {
            LOGGER.warn("Not channel to send message!");
            return;
        }
        activeChannel.writeAndFlush(messagePacket);
    }


    Channel getActiveChannel() {
        return activeChannel;
    }

    boolean hasActiveChannel() {
        return getActiveChannel() != null;
    }

    void setActiveChannel(Channel activeChannel) {
        this.activeChannel = activeChannel;
    }

    public String getStatus() {
        if (hasActiveChannel())
            return "Connected to: " + getActiveChannel().localAddress();
        return "Not connected";
    }

    private static class SingletonHolder {
        private static final GSCommunicator GS_COMMUNICATOR = new GSCommunicator();
    }

    public static GSCommunicator getInstance() {
        return SingletonHolder.GS_COMMUNICATOR;
    }
}
