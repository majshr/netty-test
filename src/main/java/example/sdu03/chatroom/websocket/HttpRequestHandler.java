package example.sdu03.chatroom.websocket;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest>{

	private final String wsUri;
	private static final File INDEX;
	static{
		URL location = HttpRequestHandler.class.getProtectionDomain().getCodeSource().getLocation();
		try{
			String path = location.toURI() + "index.html";
			path = !path.contains("file:") ? path : path.substring(5);
			INDEX = new File(path);
		} catch(URISyntaxException e){
			throw new IllegalStateException("unable to locate index.html", e);
		}
	}
	
	public HttpRequestHandler(String wsUri) {
		this.wsUri = wsUri;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		if(wsUri.equalsIgnoreCase(request.uri())){
			// （1）如果请求了 WebSocket协议升级，则增加引用计数（调用 retain()方法），并 将 它 传 递 给 下 一个ChannelInboundHandler
			// 需要调用 retain() 方法， 是因为调用 channelRead()方法完成之后，它将调用 FullHttpRequest 对象上的 release() 方法以释放它的资源。
			// SimpleChannelInboundHandler做的
			ctx.fireChannelRead(request.retain());
		} else{
			// 处理100 continue请求以符合http1.1规范
			if(HttpUtil.is100ContinueExpected(request)){
				send100Continue(ctx);
			}
			
			// 读取index.html
			RandomAccessFile file = new RandomAccessFile(INDEX, "r");
			HttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
			response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset:UTF-8");
			// 如果请求了keep-alive，添加所要的http头信息
			boolean keepAlive = HttpUtil.isKeepAlive(request);
			if(keepAlive){
				response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
				response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
			}
			
			// 将响应写到客户端
			ctx.write(response);
			
			// 将index.html写到客户端
			if(ctx.pipeline().get(SslHandler.class) == null){
				//不需要加密和压缩，零拷贝
				ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
			} else{
				ctx.write(new ChunkedNioFile(file.getChannel()));
			}
			// 写 LastHttpContent并冲刷至客户端；标记响应结束
			ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
			
			// 如果没有请求keep-alive，在写操作完成后关闭Channel
			if(!keepAlive){
				future.addListener(ChannelFutureListener.CLOSE);
			}
		}
	}
	
	/**
	 * 处理http100消息
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：</B><BR>
	 * @param ctx
	 */
	private static void send100Continue(ChannelHandlerContext ctx){
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
		ctx.writeAndFlush(response);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// 发生错误，关闭通道
		cause.printStackTrace();
		ctx.close();
	}

}
