package v1.test08.netty6heartBeat.custom;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class Server {
	
	private int port;
	
	public Server(int port) {
		super();
		this.port = port;
	}

	private void start(){
		//1, 启动两个线程 
		// 结束client端连接处理
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		// 真正处理任务, 网络通信, 网络读写
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try{
			// 2, 创建辅助工具类, 用于服务器通道的系列配置
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup) // 绑定两个线程组
			.channel(NioServerSocketChannel.class) // 指定NIO的模式
			.option(ChannelOption.SO_BACKLOG, 1024) // 设置tcp缓冲区(tcp缓冲区内核, 一般设置100多个就行)
			.option(ChannelOption.SO_SNDBUF, 32*1024)	//设置发送缓冲大小
			.option(ChannelOption.SO_RCVBUF, 32*1024)	//这是服务器端接收缓冲大小
			.option(ChannelOption.SO_KEEPALIVE, true)	//保持连接
			.handler(new LoggingHandler(LogLevel.INFO)) //设置启动时, 打印启动日志
			.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					// 设置marshalling编解码器
					ch.pipeline().addLast(MarshallingCodeFactory.buildMarshallingEncoder())
					.addLast(MarshallingCodeFactory.buildMarshllingDecoder());
					
					// 超时handler, 当服务器端与客户端在指定时间以上没有任何通信, 则会关闭响应通道, 主要为减小服务资源占用
					// 5s
					// 客户端不加也行, 但还是加上好; 两边都会计时
					ch.pipeline().addLast(new ReadTimeoutHandler(5));
					
					//3, 配置具体数据接收方法的处理
					ch.pipeline().addLast(new ServerHandler());
				}
				
			});
			
			// 4,进行绑定
			ChannelFuture f = b.bind(this.port).sync();
			System.out.println("服务已经启动, 监听端口" + this.port);
			
			// 5, 等待关闭
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			// 释放资源
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) {
		new Server(8765).start();
	}
	
}
