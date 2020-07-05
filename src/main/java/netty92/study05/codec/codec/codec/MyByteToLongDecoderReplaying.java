package netty92.study05.codec.codec.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.ReplayingDecoder;

public class MyByteToLongDecoderReplaying extends ReplayingDecoder<Void>  {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		System.out.println("====================decode=========================");
		
		/************** ReplayingDecoder不需要判断字节够不够读取 */
		out.add(in.readLong());
		
		/***************之前的程序，总是要做一些判断*****************/
		// 如果有消息可读取，会一直调用
//		if(in.readableBytes() >= 8) {
//			Long val = in.readLong();
//			out.add(val);
//		}
		
		return;
	}

}
