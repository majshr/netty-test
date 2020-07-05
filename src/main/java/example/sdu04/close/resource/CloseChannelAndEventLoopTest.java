package example.sdu04.close.resource;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.GenericFutureListener;

public class CloseChannelAndEventLoopTest {
	public static void main(String[] args) throws InterruptedException {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 100)
					.handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline p = ch.pipeline();
							p.addLast(new LoggingHandler(LogLevel.INFO));
						}
					});
			// 同步方式绑定服务监听端口
			/**
			 *  绑定端口后，main函数就不会阻塞，如果后续没有同步代码，main线程就会退出
			 *  调用b.bind(18080).sync()尽管会同步阻塞，等待端口绑定结果，但端口绑定执行非常快，完成后程序继续向下执行
			 *  finally里执行了shutdown，会关两个线程池，关闭之后，整个系统非守护线程就全部执行完了，jvm退出
			 *  
			 *  main线程退出了，jvm进程没有退出，因为此时netty的NioEventLoop还在运行状态。说明
			 *  	NioEventLoop是非守护线程
			 *  	NioEventLoop运行之后，不会主动退出
			 *  	只有调用shutdown系列方法，NioEventLoop才会退出
			 * 
			 * 	ChannelFuture作用
			 * 		通过注册监听器GenericFutureListener，可以异步等到IO执行结果
			 * 		通过sync或await，主动阻塞当前调用方的线程，等待操作结果，通常说的是异步转同步
			 */
			ChannelFuture future = b.bind(18080).sync();
			
			/**
			 * 防止netty服务端意外关闭方式1：
			 * 阻塞在CloseFuture，等待Channel关闭
			 */
//			future.channel().closeFuture().sync();
			
			/**
			 * 方式2: 通过监听方式关闭NioEventLoopGroup
			 */
			future.channel().closeFuture().addListener(new ChannelFutureListener() {
				
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					System.out.println(future.channel().toString() + "链路关闭");
					/**
					 * 防止netty服务端意外关闭方式2：
					 * 在链路关闭后，再关闭线程池
					 */
//					bossGroup.shutdownGracefully();
//					workerGroup.shutdownGracefully();
				}
			});
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}




















