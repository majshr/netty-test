package netty92.study05.codec.codec.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 解码器, 从缓冲中读取信息, 转换为Long对象
 * ==================
 * 如果缓冲中可读字节数大于8, 读取Long对象, 解码出的Long对象写到out中
 * @author bhz（maj）
 * @since 2020年7月4日
 */
public class MyByteToLongDecoder extends ByteToMessageDecoder  {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		System.out.println("====================decode=========================");
		
		// 如果有消息可读取，会一直调用
		if(in.readableBytes() >= 8) {
			Long val = in.readLong();
			out.add(val);
		}
		
		return;
	}

}
