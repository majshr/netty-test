package example.sdu02.multiplexing.executor;

import java.net.InetSocketAddress;

import io.grpc.netty.shaded.io.netty.channel.ChannelInboundHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
/**
 * 编写 Netty 应用程序的一个一般准则：尽可能地重用 EventLoop，以减少线程创建所带来的开销。
 * netty服务端接收一个客户端连接，这个客户端需要我们的服务端再向第三方服务端发起连接通信；
 * 通过新建一个Bootstrap，但是用当前新建Channel的EventLoop来处理
 * @author bhz（maj）
 * @since 2019年7月13日
 */
public class Server {
	
	// 设置用于处理已被接受的子 Channel 的 I/O 和数据的ChannelInboundHandler
	static SimpleChannelInboundHandler<ByteBuf> handler = new SimpleChannelInboundHandler<ByteBuf>(){
	
		// 新连接是否成功的future
		ChannelFuture connectFuture;
	
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			// 创建一个 Bootstrap类的实例以连接到远程主机
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.channel(NioSocketChannel.class).handler(new SimpleChannelInboundHandler<ByteBuf>() {
	
				@Override
				protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
					System.out.println("received data");
				}
			});
			// 使用与分配给已被接受的子Channel相同的EventLoop
			bootstrap.group(ctx.channel().eventLoop());
			// 连接到服务器
			connectFuture = bootstrap.connect(new InetSocketAddress("www.baidu.com", 80));
		}
	
		@Override
		protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
			if (connectFuture.isDone()) {
				// 连接完成，执行一些数据操作，比如代理等
			}
		}
	
	};
	
	public static void main(String[] args) {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
			.channel(NioServerSocketChannel.class)
			.childHandler(handler);
		
		ChannelFuture future = bootstrap.bind(8080);
		future.addListener(new ChannelFutureListener() {
			
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if(future.isSuccess()){
					System.out.println("Server bound");
				} else{
					System.out.println("bind attempt failed");
					future.cause().printStackTrace();
				}
			}
		});
	}
}
