package multiple.ways.codec.protobuf.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import multiple.ways.codec.entity.Request;
import multiple.ways.codec.entity.Response;

public class NettyServerHandler extends SimpleChannelInboundHandler<Request>{

	/**
	 * 空闲次数
	 */
	private int idle_count = 1;
	
	/**
	 * 发送次数
	 */
	private int count = 1;
	
	/**
	 * 建立连接后，服务端发送给客户端一条消息
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：</B><BR>
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("客户端地址：" + ctx.channel().remoteAddress());
		Response response = new Response();
		response.setResult("来自服务端的响应》》》我是服务端，我收到了你的连接请求！");
		
		ctx.writeAndFlush(response);
		super.channelActive(ctx);
	}
	
	/**
	 * 超时处理，如果5s没受到客户端的消息，就触发；如果超过两次，直接关闭
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：</B><BR>
	 * @param ctx
	 * @param msg
	 * @throws Exception
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof IdleStateEvent){
			IdleStateEvent event = (IdleStateEvent) evt;
			if(event.state().equals(IdleState.READER_IDLE)){
				System.out.println("5s没给我发送数据了！");
				// 已经5s没有接收到客户端数据
				if(idle_count > 1){
					System.out.println("关闭这个不活跃的channel");
					ctx.channel().close();
				}
				idle_count++;
			}
		}
	}
	

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Request msg) throws Exception {
		// 如果是请求类型
		System.out.println("第" + count + "次收到客户端消息：" + msg);
		if(msg instanceof Request){
			System.out.println("来自客户端的请求》》》》" + msg.getClassName() + "--" + msg.getMethodName());
		}
		count++;
	}
	
	/**
	 * 
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：</B><BR>
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
