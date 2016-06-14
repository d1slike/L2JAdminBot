package ru.disdev.network;

import io.netty.bootstrap.ServerBootstrap;
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
import ru.disdev.network.objects.MessageDecoder;
import ru.disdev.network.objects.MessageEncoder;
import ru.disdev.network.objects.MessagePacket;

import java.io.File;
import java.util.concurrent.TimeUnit;


/**
 * Created by DisDev on 04.05.2016.
 */
public class GSCommunicator {

    private static final Logger LOGGER = LogManager.getLogger(GSCommunicator.class);

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final ServerBootstrap serverBootstrap;

    public GSCommunicator() {
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
                            handler.setHandshakeTimeout(3, TimeUnit.SECONDS);
                            handler.setCloseNotifyTimeout(1, TimeUnit.SECONDS);
                            channelPipeline.addFirst(handler);
                            channelPipeline.addLast(new MessageEncoder(), new MessageDecoder(), MessageHandler.getInstance());
                        }
                    });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.serverBootstrap = serverBootstrap;
    }

    public void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public void sendMessageToGameServer(MessagePacket messagePacket) {
        MessageHandler.getInstance().writeMessage(messagePacket);
    }

    public void start() {
        //InetSocketAddress address = new InetSocketAddress(Config.BOT_HOST, Config.BOT_PORT);
        serverBootstrap.bind(Config.BOT_PORT).addListener(future -> {
            if (future.cause() == null)
                LOGGER.info("BotServer is up. Listening gameserver...");
            else
                LOGGER.error("Error while binding bot server", future.cause());
        });
    }
}
