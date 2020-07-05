package netty92.study05.codec.unpackingAndSticking.resolve.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import netty92.study05.codec.unpackingAndSticking.resolve.protocal.PersonProtocol;
/**
 * PersonProtocol编码器
 * @author bhz（maj）
 * @since 2020年7月4日
 */
public class MyPersonEncoder extends MessageToByteEncoder<PersonProtocol> {

	@Override
	protected void encode(ChannelHandlerContext ctx, PersonProtocol msg, ByteBuf out) throws Exception {
		System.out.println("MyPersonEncoder encode invoked!");
		// 一个int表示内容长度
		out.writeInt(msg.getLength());
		// 具体内容
		out.writeBytes(msg.getContent());
	}

}
