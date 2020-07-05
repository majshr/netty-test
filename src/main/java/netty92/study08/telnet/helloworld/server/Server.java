package netty92.study08.telnet.helloworld.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class Server {
	public static void main(String[] args) {
		// Configure the server
		// 创建两个EventLoopGroup对象
		// 创建boss线程组 用于服务端接受客户端的连接
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		// 创建 worker 线程组 用于进行 SocketChannel 的数据读写
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		ServerBootstrap b = new ServerBootstrap();
		
		try{
			b.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.handler(new LoggingHandler(LogLevel.INFO))
			// Server端主Channel用来管理客户端的连接；childHandler处理具体连接
			.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline pipeline = ch.pipeline();
					// 添加帧限定符来防止粘包现象
					// 以("\n")为结尾分割的 解码器
					pipeline.addLast(new DelimiterBasedFrameDecoder(1000000, Delimiters.lineDelimiter()));
					// 解码器和编码器，和客户端一样
					pipeline.addLast(new StringDecoder());
					pipeline.addLast(new StringEncoder());
					
					pipeline.addLast(new IdleStateHandler(5, 0, 0));
					
					// 业务逻辑
					pipeline.addLast(new ServerHandler());
				}
				
			});
			
			// 同步到绑定完成(绑定需要设置本地域名，否则用localhost找不到)
			// 且使用channelFuture.channel().closeFuture().sync();方法后关闭线程组方式没生效，直接关闭了程序
			// 使用监听器才没关闭程序
			ChannelFuture channelFuture = b.bind("localhost", 8888).sync();
			// 主Channel关闭后，才能关闭线程组
//			channelFuture.channel().closeFuture().addListener(new ChannelFutureListener() {
//				
//				@Override
//				public void operationComplete(ChannelFuture future) throws Exception {
//					if(future.isDone()){
//						bossGroup.shutdownGracefully();
//						workerGroup.shutdownGracefully();
//					}
//				}
//			});
			channelFuture.channel().closeFuture().sync();
			
		}catch(Exception e){
			
		}finally{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
