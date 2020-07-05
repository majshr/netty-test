package netty92.study05.codec.unpackingAndSticking.resolve;

import java.nio.charset.Charset;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import netty92.study05.codec.unpackingAndSticking.resolve.protocal.PersonProtocol;

public class MyClientHandler extends SimpleChannelInboundHandler<PersonProtocol> {

	private int count;
	static final Charset UTF8 = Charset.forName("utf-8"); 
	
	// 连接建立成功后，向客户端发送一个long类型数据
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		for(int i = 0; i < 10; ++i) {
			String sendStr = "sent from client";
			
			PersonProtocol person = new PersonProtocol();
			person.setContent(sendStr.getBytes(UTF8));
			person.setLength(person.getContent().length);
			
			ctx.writeAndFlush(person);
		}
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PersonProtocol msg) throws Exception {
		
		String message = new String(msg.getContent(), Charset.forName("utf-8"));
		System.out.println("客户端接收到消息：" + message);
		System.out.println("客户端收到消息数量：" + (++this.count));
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
