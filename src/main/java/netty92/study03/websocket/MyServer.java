package netty92.study03.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class MyServer {
	public static void main(String[] args) throws InterruptedException {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try{
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.handler(new LoggingHandler(LogLevel.INFO)) // 日志信息
			.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					// websocket基于http
					ch.pipeline().addLast(new HttpServerCodec());
					// 以块的方式写
					ch.pipeline().addLast(new ChunkedWriteHandler());
					// 分段请求聚合到一起，
					// http转换解析器(http消息的多个部分合并成一个完整地http消息, post请求就需要)
					// 如：netty处理发送请求长度为1000，分为n个段，每段为100；这个过滤器将所有请求聚合到一起，成为一个完整http请求
					ch.pipeline().addLast(new HttpObjectAggregator(8192));
					// ws://ip:port/context_path   websocket协议
					// 访问本地ws路径：ws://ip:port/ws
					// 一定要带ws前的"/"，否则连接建立不成功！！！！！
					ch.pipeline().addLast(new WebSocketServerProtocolHandler("/ws"));
					
					ch.pipeline().addLast(new TextWriteFrameWebsocket());
					

				}
				
				private void initNetWork(SocketChannel ch){
			        ChannelPipeline pipeline = ch.pipeline();
			        //HttpServerCodec: 针对http协议进行编解码
			        pipeline.addLast("httpServerCodec", new HttpServerCodec());
			        //ChunkedWriteHandler分块写处理，文件过大会将内存撑爆
			        pipeline.addLast("chunkedWriteHandler", new ChunkedWriteHandler());
			        /**
			         * 作用是将一个Http的消息组装成一个完成的HttpRequest或者HttpResponse，那么具体的是什么
			         * 取决于是请求还是响应, 该Handler必须放在HttpServerCodec后的后面
			         */
			        pipeline.addLast("httpObjectAggregator", new HttpObjectAggregator(8192));
			        
			        //用于处理websocket, /ws为访问websocket时的uri
			        pipeline.addLast("webSocketServerProtocolHandler", new WebSocketServerProtocolHandler("/ws"));
			        
			        pipeline.addLast("myWebSocketHandler", new TextWriteFrameWebsocket());
				}
				
			});
			
			ChannelFuture channelFuture = serverBootstrap.bind(8899).sync();
			channelFuture.channel().closeFuture().sync();
			
		}finally{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}












































