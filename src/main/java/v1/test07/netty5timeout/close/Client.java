package v1.test07.netty5timeout.close;

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
			System.out.println("连接客户端成功!");
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
		
		client.connect();
		
		// 发送信息, 每次发送完成, 休眠4s, 连接断开时间为5s, 所以不会断开
		ChannelFuture cf = client.getChannelFuture();
		for(int i = 0; i < 3; i++){
			Request request = new Request();
			request.setId("" + i);
			request.setName("pro" + i);
			request.setRequestMessage("数据信息" + i);
			cf.channel().writeAndFlush(request);
			Thread.sleep(4000);
		}
		
		// 程序走到这, 说明现在客户端和server端的服务断开了,
        // 现在client这个对象还是存在的, 只是通道断开了
		// 需要重新连接
		cf.channel().closeFuture().sync();
		
		// 断开连接后, 想继续发送信息
		// 启动一个新线程, 重新连接
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("进入子程序......");
				// 管道是否激活中, 是否打开(此时应该是关闭的)
				System.out.println(cf.channel().isActive());
				System.out.println(cf.channel().isOpen());
				
				// 重新连接, 发送信息
				client.connect();
				ChannelFuture cf = client.getChannelFuture();
				Request request = new Request();
				request.setId("" + 4);
				request.setName("pro" + 4);
				request.setRequestMessage("数据信息" + 4);
				cf.channel().writeAndFlush(request);	
				
				// 发送完信息后, 阻塞到这, 再关闭通道后, 向下执行
				try {
					cf.channel().closeFuture().sync();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("子线程结束.");
			}
			
		}).start();
		
	}

}
