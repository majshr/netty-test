package netty92.study05.codec.codec.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
/**
 * 编码器, 将Long编码为字节
 * @author bhz（maj）
 * @since 2020年7月3日
 */
public class MyLongToByteEncoder extends io.netty.handler.codec.MessageToByteEncoder<Long> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Long msg, ByteBuf out) throws Exception {
		out.writeLong(msg);
	}

}
