package netty92.study08.telnet.helloworld.server;

import java.net.InetAddress;
import java.util.Date;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
/**
 * channelActive: 建立连接后, 发送welcome给客户端<br>
 * userEventTriggered: 心跳检测, 5s没有收到消息, 断开连接<br>
 * channelRead0: 根据收到消息, 做出回应<br>
 * @author bhz（maj）
 * @since 2020年7月4日
 */
public class ServerHandler extends SimpleChannelInboundHandler<String>{

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.write("welcome to " + InetAddress.getLocalHost().getHostName() + "!\r\n");
		ctx.write("it is " + new Date() + " new. \r\n");
		ctx.flush();
	
	}
	
	/**
	 * 心跳检测超时触发
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：</B><BR>
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#userEventTriggered(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof IdleStateEvent){
			IdleStateEvent idleEvent = (IdleStateEvent) evt;
			if(idleEvent.equals(IdleStateEvent.READER_IDLE_STATE_EVENT)){
				System.out.println("5s没有收到客户端消息了。");
				ctx.channel().close();
			}
		}
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		String response;
		boolean close = false;
		
		if(msg.isEmpty()){
			response = "please type something. \r\n";
		} else if("bye".equals(msg.toLowerCase())){
			response = "Have a good day, bye! \r\n";
			close = true;
		} else {
			response = "did you say '"+ msg + "'? \r\n";
		}
		
		
		// 发送响应，
		ChannelFuture channelFuture = ctx.writeAndFlush(response);
		// 监听相应完成，完成后关闭连接
		if(close)
			channelFuture.addListener(ChannelFutureListener.CLOSE);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		ctx.close();
	}

}
