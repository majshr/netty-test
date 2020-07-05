package netty92.study05.codec.unpackingAndSticking.resolve;

import java.nio.charset.Charset;
import java.util.UUID;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import netty92.study05.codec.unpackingAndSticking.resolve.protocal.PersonProtocol;

public class MyServerHandler extends SimpleChannelInboundHandler<PersonProtocol> {

	private int count;
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PersonProtocol msg) throws Exception {
		
		String message = new String(msg.getContent(), Charset.forName("utf-8"));
		
		System.out.println("服务端收到消息---->>>长度：" + msg.getLength() + "    内容：" + message);
		System.out.println("服务端收到消息数量：" + (++count));
		
		PersonProtocol response = new PersonProtocol();
		String responseStr = UUID.randomUUID().toString();
		response.setContent(responseStr.getBytes(Charset.forName("utf-8")));
		response.setLength(response.getContent().length);
		
		ctx.writeAndFlush(response);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
