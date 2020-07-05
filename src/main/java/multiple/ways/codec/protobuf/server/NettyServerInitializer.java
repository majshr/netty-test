package multiple.ways.codec.protobuf.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import multiple.ways.codec.codec.IMessageCodecUtil;
import multiple.ways.codec.codec.MessageDecoder;
import multiple.ways.codec.codec.MessageEncoder;
import multiple.ways.codec.protobuf.codec.ProtoBufCodecUtil;
import multiple.ways.codec.protobuf.serialize.ProtoBufSerialize;

public class NettyServerInitializer extends ChannelInitializer<SocketChannel>{

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		// 5s没有读取到数据触发
		ch.pipeline().addLast(new IdleStateHandler(3, 0, 0));
		
//		MessageCodecUtil util = new ProtoBufCodecUtil(new ProtoBufRpcSerialize(false));
		IMessageCodecUtil util = new ProtoBufCodecUtil(ProtoBufSerialize.getInstance(), false);
		
		ch.pipeline().addLast(new MessageEncoder(util));
		ch.pipeline().addLast(new MessageDecoder(util));
		
		ch.pipeline().addLast(new NettyServerHandler());
	}

}
