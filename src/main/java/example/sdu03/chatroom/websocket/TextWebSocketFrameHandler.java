package example.sdu03.chatroom.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler.HandshakeComplete;

public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>{

	private final ChannelGroup group;
	
	public TextWebSocketFrameHandler(ChannelGroup group) {
		this.group = group;
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE){
			// 收到事件，表示握手成功，从ChannelPipline中移除HttpRequestHandler，因为将不会再收到http消息了
			ctx.pipeline().remove(HttpRequestHandler.class);
			
			// 通知所有已经连接的WebSocket，新连接来了
			group.writeAndFlush(new TextWebSocketFrame("client" + ctx.channel() + "joined"));
			
			//新的WebSocket Channel放到channelgroup中
			group.add(ctx.channel());
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
		// 增加消息引用计数，并将它写到ChannelGroup中已连接的客户端
		/*对于 retain() 方法的调用是必需的，因为当 channelRead0() 方法返回时，
		TextWebSocketFrame 的引用计数将会被减少。由于所有的操作都是异步的，因此， writeAndFlush() 方法可能会在 channelRead0() 方法返回之后完成，而且它绝对不能访问一个已经失
		效的引用。*/
		group.writeAndFlush(msg.retain());
	}

}
