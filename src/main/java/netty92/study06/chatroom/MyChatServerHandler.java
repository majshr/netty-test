package netty92.study06.chatroom;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 广播实现服务器广播给所有连接者
 * <B>系统名称：</B><BR>
 * <B>模块名称：</B><BR>
 * <B>中文类名：</B><BR>
 * <B>概要说明：</B><BR>
 * @author bhz（maj）
 * @since 2019年5月25日
 */
public class MyChatServerHandler extends SimpleChannelInboundHandler<String>{

	/**
	 *  netty会自动调用，设置进来值（保存所有通道信息）
	 *  handlerAdded时手动添加channel；handlerRemoved时会自动移除channel
	 */
	private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		Channel channel = ctx.channel();
		System.out.println("【" + channel.remoteAddress() + "】" + "发送消息：" + msg);
		System.out.println(channelGroup.size());
		for(Channel ch : channelGroup){
			// "\n是必须的，否则消息发送给Client后显示不出来，需要回车后才能显示（当时填写内容并回车显示了内容）"
			if(channel != ch){
				ch.writeAndFlush("【" + channel.remoteAddress() + "】" + "发送消息：" + msg + "\n"); 
			} else{
				ch.writeAndFlush("【自己】" + msg + "\n");
			}
		}
	}
	
	/**
	 * 一个客户端与服务端建立连接后触发
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：</B><BR>
	 * @see io.netty.channel.ChannelHandlerAdapter#handlerAdded(io.netty.channel.ChannelHandlerContext)
	 */
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		
		// 遍历所有channel，发送信息
		channelGroup.writeAndFlush("【服务器】-" + channel.remoteAddress() + "加入\n");
		
		// 保存channel
		channelGroup.add(channel);
	}
	
	/**
	 * 连接处于活动状态
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：</B><BR>
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelActive(io.netty.channel.ChannelHandlerContext)
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		System.out.println(channel.remoteAddress() + "上线");
	}
	
	/**
	 * 连接出于没活动
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：</B><BR>
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelInactive(io.netty.channel.ChannelHandlerContext)
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		System.out.println(channel.remoteAddress() + "下线");
	}
	
	/**
	 * 连接断掉
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：</B><BR>
	 * @see io.netty.channel.ChannelHandlerAdapter#handlerRemoved(io.netty.channel.ChannelHandlerContext)
	 */
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		
		// channel会被group自动移除
		// 因为连接已经断开了，所以也不会给断开的客户端发送这个消息了
		channelGroup.writeAndFlush("【服务器-】" + channel.remoteAddress() + "离开\n");
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}





