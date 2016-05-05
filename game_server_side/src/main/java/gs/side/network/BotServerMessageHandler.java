package gs.side.network;

import gs.side.network.objects.MessagePacket;
import gs.side.request.impl.AbstractRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import gs.side.request.RequestHolder;

import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Admin on 04.05.2016.
 */
public class BotServerMessageHandler extends SimpleChannelInboundHandler<MessagePacket> {

    private static final Lock lock = new ReentrantLock();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessagePacket s) throws Exception {
        lock.lock();
        try {
            final String command = s.getMessage();
            Optional<AbstractRequest> abstractRequest = RequestHolder.getInstance().get(command);
            String answer = abstractRequest.isPresent() ? abstractRequest.get().execute(command) : "Command not found. Please use /help";
            channelHandlerContext.writeAndFlush(new MessagePacket(s.getChatId(), answer));
        } finally {
            lock.unlock();
        }

    }
}
