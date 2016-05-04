package network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

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

                            // Add SSL handler first to encrypt and decrypt everything.
                            // In this example, we use a bogus certificate in the server side
                            // and accept any invalid certificates in the client side.
                            // You will need something more complicated to identify both
                            // and server in the real world.
                            pipeline.addLast(sslCtx.newHandler(socketChannel.alloc(), "127.0.0.1", 7777));

                            // On top of the SSL handler, add the text line codec.
                            pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());

                            // and then business logic.
                            pipeline.addLast(new BotServerMessageHandler());
                        }
                    });
            bootstrap.connect("127.0.0.1", 7777).sync();
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
