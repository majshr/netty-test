package netty92.study04.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
/**
 * 读写事件空闲时间内没有进行, 触发回调
 * @author bhz（maj）
 * @since 2020年7月3日
 */
public class MyServerHandler extends ChannelInboundHandlerAdapter{
	/**
	 * 转发给pipline的下一个handler
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：</B><BR>
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#userEventTriggered(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof IdleStateEvent){
			IdleStateEvent event = (IdleStateEvent) evt;
			String eventType = null;
			// state返回一个枚举IdelState，读空闲，写空闲，all空闲
			switch(event.state()){
			case READER_IDLE:{
				eventType = "读空闲";
				break;
			}
			case WRITER_IDLE:{
				eventType = "写空闲";
				break;
			}
			case ALL_IDLE:{
				eventType = "读写空闲";
				break;
			}
			}
			
			System.out.println(ctx.channel().remoteAddress() + "超时事件：" + eventType);
			ctx.channel().close();
		}
	}
}

























