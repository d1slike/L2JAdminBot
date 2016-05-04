package ru.disdev.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.disdev.Cfg;

import java.nio.charset.Charset;

/**
 * Created by DisDev on 04.05.2016.
 */
public class GSCommunicator {

    private static final Logger LOGGER = LogManager.getLogger(GSCommunicator.class);

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final MessageHandler messageHandler;
    private final ServerBootstrap serverBootstrap;

    public GSCommunicator() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        messageHandler = new MessageHandler();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            SelfSignedCertificate selfSignedCertificate = new SelfSignedCertificate();
            SslContext sslContext = SslContextBuilder.forServer(selfSignedCertificate.certificate(),
                    selfSignedCertificate.privateKey()).build();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline channelPipeline = socketChannel.pipeline();
                            channelPipeline.addLast(sslContext.newHandler(socketChannel.alloc()));
                            channelPipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                            channelPipeline.addLast(new StringEncoder(Charset.forName("UTF-8")));
                            channelPipeline.addLast(new StringDecoder(Charset.forName("UTF-8")));
                            channelPipeline.addLast(messageHandler);
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

    public void sendMessageToGameServer(String command) {
        messageHandler.writeMessage(command);
    }

    public void start() {
        LOGGER.info("BotServer is up. Listening gameserver...");
        try {
            serverBootstrap.bind(Cfg.BOT_HOST, Cfg.BOT_PORT).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
