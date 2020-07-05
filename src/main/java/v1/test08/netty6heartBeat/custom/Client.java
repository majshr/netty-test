package v1.test08.netty6heartBeat.custom;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class Client {
	private EventLoopGroup group;
	private Bootstrap b;
	private ChannelFuture cf;
	private String host;
	private int port;
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	static class Instance{
		public static Client instance = new Client();
	}
	
	/**
	 * 获取ChannelFuture对象(如果channel关闭, 重新连接)
	 * @return
	 */
	public ChannelFuture getChannelFuture(){
		return cf;
	}
	
	public EventLoopGroup getEventLoopGroup() {
		return group;
	}

	public Bootstrap getBootstrap() {
		return b;
	}

	/**
	 * 客户端单例
	 */
	public static Client getInstance(){
		return Instance.instance;
	}
	
	/**
	 * 初始化时, 创建连接
	 */
	private Client(){
		group = new NioEventLoopGroup();
		b = new Bootstrap();
		b.group(group)
		.channel(NioSocketChannel.class)
		.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel sc) throws Exception {
				// 设置marshalling编解码器
				sc.pipeline().addLast(MarshallingCodeFactory.buildMarshallingEncoder())
				.addLast(MarshallingCodeFactory.buildMarshllingDecoder());
				
				// 超时handler, 当服务器端与客户端在指定时间以上没有任何通信, 则会关闭响应通道, 主要为减小服务资源占用
				// 5s
				// 客户端不加也行, 但还是加上好; 两边都会计时
				sc.pipeline().addLast(new ReadTimeoutHandler(5));
				
				sc.pipeline().addLast(new ClientHandler());
			}
		});
	}
	
	/**
	 * 连接方法
	 * @param host
	 * @param port
	 */
	private void connect(String host, int port){
		try {
			cf = b.connect(host, port).sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 连接服务端
	 */
	public void connect(){
		connect(host, port);
	}
	
	public static void main(String[] args) throws Exception{
		// 连接服务端, 连接成功后, 会设置好ChannelFuture值
		Client client =  getInstance();
		String host = "localhost";
		int port = 8765;
		client.setHost(host);
		client.setPort(port);
		
		// 连接, 获取ChannelFuture
		client.connect();
		ChannelFuture cf = client.getChannelFuture();
		
		// 程序走到这, 说明现在客户端和server端的服务断开了,
        // 现在client这个对象还是存在的, 只是通道断开了
		cf.channel().closeFuture().sync();
		client.getEventLoopGroup().shutdownGracefully();
	}

}
