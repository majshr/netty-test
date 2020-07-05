package v1.test04.netty1hello.paramExplain;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class HelloClient {
	private int port;
	public HelloClient(int port) {
		this.port = port;
	}
	
	public void start() throws InterruptedException{
		// 客户端仅发送请求, 不需要处理请求连接的线程组
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(group)
		.channel(NioSocketChannel.class)
		.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel sc) throws Exception {
				sc.pipeline().addLast(new HelloClientHandler());
			}
		});
		
		// 连接服务端
		ChannelFuture cf1 = b.connect("127.0.0.1", port).sync();
		System.out.println("连接" + port + ".....");
		
		//发送消息 cf.channel() 获取通信管道
		// 777 666是一起发送到服务端的(拆包粘包)
		System.out.println("开始发送数据......");
		cf1.channel().writeAndFlush(Unpooled.copiedBuffer("777".getBytes()));
		System.out.println("发送数据完成......");
		
		cf1.channel().writeAndFlush(Unpooled.copiedBuffer("666".getBytes()));
//		cf2.channel().writeAndFlush(Unpooled.copiedBuffer("888".getBytes()));
		
		// 发的间隔之间休眠几秒, 可以将发送信息分开
		Thread.sleep(2000);
		cf1.channel().writeAndFlush(Unpooled.copiedBuffer("888".getBytes()));
		//cf2.channel().writeAndFlush(Unpooled.copiedBuffer("666".getBytes()));
		
		cf1.channel().closeFuture().sync();
		//cf2.channel().closeFuture().sync();
		group.shutdownGracefully();
	}
	
	public static void main(String[] args) throws Exception{
		new HelloClient(8765).start();
	}
}
