package example.sdu03.chatroom.websocket;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

public class ChatServer {
	private final ChannelGroup channelGroup = 
			new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
	
	private final EventLoopGroup group = new NioEventLoopGroup();
	
	/**
	 * Server监听连接的channel
	 */
	private Channel channel;
	
	public ChannelFuture start(InetSocketAddress address) {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(group).channel(NioServerSocketChannel.class).childHandler(createInitializer(channelGroup));
		ChannelFuture future = bootstrap.bind(address);
		future.syncUninterruptibly();
		channel = future.channel();
		return future;
	}
	
	/**
	 * 创建handler
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：</B><BR>
	 * @param group
	 * @return
	 */
	protected ChannelInitializer<Channel> createInitializer(ChannelGroup group) {
		return new ChatServerInitializer(group);
	}
	
	/**
	 * 处理服务器关闭，并释放所有资源
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：</B><BR>
	 */
	public void destory(){
		if(channel != null){
			channel.close();
		}
		channelGroup.close();
		group.shutdownGracefully();
	}
	
	public static void main(String[] args) {
		int port = 8080;
		final ChatServer endpoint = new ChatServer();
		ChannelFuture future = endpoint.start(new InetSocketAddress(port));
		
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				endpoint.destory();
			}
		});
		
		future.channel().closeFuture().syncUninterruptibly();
	}
}
