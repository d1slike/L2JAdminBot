package gs.side.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import gs.side.network.objects.MessageDecoder;
import gs.side.network.objects.MessageEncoder;

/**
 * Created by Admin on 04.05.2016.
 */
public class L2JAdminBotClient {
    private final EventLoopGroup bossGroup;

    public L2JAdminBotClient() {
        bossGroup = new NioEventLoopGroup();
        try {
            final SslContext sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(bossGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //todo check ssl
                            pipeline.addLast(sslCtx.newHandler(socketChannel.alloc(), "127.0.0.1", 9191));
                            pipeline.addLast(new MessageEncoder(), new MessageDecoder(), new BotServerMessageHandler());
                        }
                    });
            bootstrap.connect("127.0.0.1", 9191).sync();
        } catch (Exception ex) {

        }
    }

    public void shutdown() {
        bossGroup.shutdownGracefully();
    }

    public static void main(String... srgs) {
        new L2JAdminBotClient();
    }
}
