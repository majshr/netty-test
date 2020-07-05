package v1.test03.echo.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * 接收处理服务端传来的数据<br>
 * 
 * ChannelInboundHandlerAdapter在处理完消息后需要负责释放资源。
 * 在这里将调用ByteBuf.release()来释放资源。<br>
 * 
 * SimpleChannelInboundHandler会在完成channelRead0后释放消息，这是通过Netty处理所有消息的
 * ChannelHandler实现了ReferenceCounted接口达到的。
 * 
 * @author maj
 *
 */
public class EchoClientHandler extends  SimpleChannelInboundHandler<ByteBuf>{


	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.write(Unpooled.copiedBuffer("netty rocks", CharsetUtil.UTF_8));
		ctx.flush();
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
//		System.out.println("client reveived: " + ByteBufUtil.hexDump(msg.readBytes(msg.readableBytes())));
		System.out.println("client reveived: " + msg.toString(CharsetUtil.UTF_8));
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}




















