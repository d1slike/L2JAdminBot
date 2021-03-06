package gs.side.network.objects;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by DisDev on 05.05.2016.
 */
public class MessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        long chatId = in.readLong();
        byte[] messageInBytes = new byte[in.readableBytes()];
        in.readBytes(messageInBytes);
        String message = new String(messageInBytes, "UTF-8");
        out.add(new MessagePacket(chatId, message));
    }
}
