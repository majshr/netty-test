package v1.test05.netty2unpacking.fixedlength.characters;

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
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class Client {
	private int port;
	public Client(int port) {
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
				// ************先按字节长度截取, 再转换为字符串*************
				// 长度随便指定的, 只是够5位, 当成同一块数据
				sc.pipeline().addLast(new FixedLengthFrameDecoder(5));;
				// 设置字符串形式的解码(客户端发送消息时, 可以直接发送字符串)
				sc.pipeline().addLast(new StringDecoder());
				
				sc.pipeline().addLast(new ClientHandler());
			}
		});
		
		// 连接服务端
		ChannelFuture cf1 = b.connect("127.0.0.1", port).sync();
		System.out.println("连接" + port + ".....");
		// 发数据的时候还是需要发送buf, 发到另一端后会直接解析为字符串
		//发送消息 cf.channel() 获取通信管道
		cf1.channel().writeAndFlush(Unpooled.copiedBuffer("7777777".getBytes()));
		cf1.channel().writeAndFlush(Unpooled.copiedBuffer("88888".getBytes()));
		
		cf1.channel().closeFuture().sync();
		group.shutdownGracefully();
	}
	
	public static void main(String[] args) throws Exception{
		new Client(8765).start();
	}
}
