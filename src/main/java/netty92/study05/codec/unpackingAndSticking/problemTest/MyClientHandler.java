package netty92.study05.codec.unpackingAndSticking.problemTest;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MyClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

	private int count;
	
	// 连接建立成功后，向服务端发送一个string类型数据
	// 服务端收到的消息是黏到一起的, 不能区分多次发送
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		for(int i = 0; i < 10; ++i) {
			ByteBuf buf = Unpooled.copiedBuffer("sent from client", Charset.forName("utf-8"));
			ctx.writeAndFlush(buf);
		}
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		byte[] buffer = new byte[msg.readableBytes()];
		msg.readBytes(buffer);
		
		String message = new String(buffer, Charset.forName("utf-8"));
		System.out.println("客户端接收到消息：" + message);
		System.out.println("客户端收到消息数量" + (++this.count));
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
