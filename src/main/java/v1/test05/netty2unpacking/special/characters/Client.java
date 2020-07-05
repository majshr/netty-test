package v1.test05.netty2unpacking.special.characters;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class Client {
	private int port;
	public Client(int port) {
		this.port = port;
	}
	public void start() throws InterruptedException{
		// 
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(group)
		.channel(NioSocketChannel.class)
		.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel sc) throws Exception {
				// 
//				ByteBuf buf = Unpooled.copiedBuffer("$_".getBytes());
//				sc.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, buf))
//				.addLast(new StringDecoder());
				
				// 特殊字符作为分隔符
				ByteBuf buf = Unpooled.copiedBuffer("$_".getBytes());
				// 处理器
				sc.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, buf));
				// 转为string
				sc.pipeline().addLast(new StringDecoder());
				
				sc.pipeline().addLast(new ClientHandler());
			}
		});
		
		// 
		ChannelFuture cf1 = b.connect("127.0.0.1", port).sync();
		// 发送信息
		cf1.channel().writeAndFlush(Unpooled.copiedBuffer("777$_".getBytes()));
		// 发送信息
//		Thread.sleep(2000);
		cf1.channel().writeAndFlush(Unpooled.copiedBuffer("666$_".getBytes()));
		cf1.channel().writeAndFlush(Unpooled.copiedBuffer("888$_".getBytes()));
		
		cf1.channel().closeFuture().sync();
		group.shutdownGracefully();
	}
	
	public static void main(String[] args) throws Exception{
		new Client(8765).start();
	}
}
