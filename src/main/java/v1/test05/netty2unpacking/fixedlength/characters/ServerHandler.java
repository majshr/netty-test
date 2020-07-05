package v1.test05.netty2unpacking.fixedlength.characters;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 读取客户端发来的数据, 向客户端响应数据
 * @author bhz（maj）
 * @since 2020年7月2日
 */
public class ServerHandler extends ChannelInboundHandlerAdapter{
	
	/**
	 * 通道激活
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("server channel active... ");
	}


	/**
	 * 接收到客户端传来的数据
	 * @param ctx
	 * @param msg
	 * @throws Exception
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// 解析自动解析成字符串
		String body = (String) msg;
		System.out.println("Server :" + body );
		
		// 给客户端相应信息是异步的, 可以通过添加回调方法, 知道是否发送信息完成
		ChannelFuture f = ctx.writeAndFlush(Unpooled.copiedBuffer(body.getBytes()));
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx)
			throws Exception {
		
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable t)
			throws Exception {
		ctx.close();
	}
	
}
































