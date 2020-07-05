package v1.test02.http.websocket.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import v1.test02.http.websocket.handler.HttpHandler;
import v1.test02.http.websocket.handler.WebSocketHandler;

public class ChatServer {
	private int port;
	
	public ChatServer(int port) {
		super();
		this.port = port;
	}

	public void start(){
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try{
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 1024)
			.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					/*************编码和解码http请求***********************/
					// 请求和相应码解析为http消息(HttpRequestDecoder 和 HttpResponseEncoder 的结合)
					/* 但是只能解析http get请求(参数包含在uri中, 因此通过HttpRequest就能解析出请求参数.),
					对于http post请求, 参数信息放在message body中, 对应netty来说是HttpMessage, 所以这个编码器不能完全解析post
					HTTP POST 请求时，请务必在 ChannelPipeline 中加上 HttpObjectAggregator(可以把 HttpMessage 和 HttpContent
					 聚合成一个 FullHttpRequest 或者 FullHttpResponse （取决于是处理请求还是响应），而且它还可以帮
					 助你在解码时忽略是否为“块”传输方式。)
					*/
					ch.pipeline().addLast(new HttpServerCodec());
					// http转换解析器(http消息的多个部分合并成一个完整地http消息)
					ch.pipeline().addLast(new HttpObjectAggregator(64 * 1024));
					// 用于处理文件流一个handler, 写出文件解析器(向客户端发送html5文件)
					ch.pipeline().addLast(new ChunkedWriteHandler());
					//自定义处理器(上边的编解码器将请求解析为一个FullHttpRequest, 此处来处理)
					ch.pipeline().addLast(new HttpHandler());
					
					/*************支持websocket协议*************/
					// ws://im都认为是websocket的东西
					ch.pipeline().addLast(new WebSocketServerProtocolHandler("/im"));
					ch.pipeline().addLast(new WebSocketHandler());
				}
				
			});
			
			ChannelFuture f = b.bind(this.port).sync();
			System.out.println("服务已经启动, 监听端口" + this.port);
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) {
		new ChatServer(8080).start();
	}
}








