package v1.test04.netty1hello.paramExplain;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
/**
 * 接收客户端发来的数据, 打印; 然后对客户端进行回应
 * @author bhz（maj）
 * @since 2020年7月2日
 */
public class HelloServerHandler extends ChannelInboundHandlerAdapter{

	
	/**
	 * 通道激活
	 * @param ctx
	 * @throws Exception
	 */
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
		ByteBuf buf = (ByteBuf) msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req, "utf-8");
		System.out.println("Server :" + body );
		
		String response = "进行返回给客户端的响应：" + body ;
		// 给客户端相应信息是异步的, 可以通过添加回调方法, 知道是否发送信息完成
		ChannelFuture f = ctx.writeAndFlush(Unpooled.copiedBuffer(response.getBytes()));
		
		// 表示服务端发送信息完成后关闭连接 
//		f.addListener(ChannelFutureListener.CLOSE);
		
		// 清除消息
		ReferenceCountUtil.release(msg);
	}

//	public void channelReadComplete(ChannelHandlerContext ctx)
//			throws Exception {
//		System.out.println("读完了");
//		ctx.flush();
//	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable t)
			throws Exception {
		ctx.close();
	}
	
}
































