package netty92.study05.codec.unpackingAndSticking.resolve.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import netty92.study05.codec.unpackingAndSticking.resolve.protocal.PersonProtocol;
/**
 * Person解码器
 * @author bhz（maj）
 * @since 2020年7月4日
 */
public class MyPersonDecoder extends ReplayingDecoder<Void> {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		System.out.println("MyPersonDecoder decode invoked!");
		// 先读取长度
		int length = in.readInt();
		
		// 再根据内容长度读取内容
		byte[] content = new byte[length];
		in.readBytes(content);
		
		PersonProtocol person = new PersonProtocol();
		person.setLength(length);
		person.setContent(content);
		
		out.add(person);
	}

}
