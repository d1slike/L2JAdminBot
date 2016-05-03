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
import ru.disdev.Cfg;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Created by Dislike on 03.05.2016.
 */
public class Server {

    private static final StringDecoder DECODER = new StringDecoder(Charset.forName("UTF-8"));
    private static final StringEncoder ENCODER = new StringEncoder(Charset.forName("UTF-8"));
    private static final IOHandler IO_HANDLER = new IOHandler();

    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup(1);

    public Server() throws InterruptedException {
        final InetSocketAddress address = new InetSocketAddress(Cfg.GS_HOST, Cfg.GS_PORT);

        final ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        final ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                        pipeline.addLast("decoder", DECODER);
                        pipeline.addLast("encoder", ENCODER);
                        pipeline.addLast("handler", IO_HANDLER);

                    }
                });

        b.bind(address).sync();

    }

    public void sendMessageToGameServer(String message) {
        IO_HANDLER.sendMessage(message);
    }

    public void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

}
