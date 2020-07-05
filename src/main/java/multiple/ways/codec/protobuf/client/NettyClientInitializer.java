package multiple.ways.codec.protobuf.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import multiple.ways.codec.codec.IMessageCodecUtil;
import multiple.ways.codec.codec.MessageDecoder;
import multiple.ways.codec.codec.MessageEncoder;
import multiple.ways.codec.protobuf.codec.ProtoBufCodecUtil;
import multiple.ways.codec.protobuf.rpcserialize.ProtoBufRpcSerialize;
import multiple.ways.codec.protobuf.serialize.ProtoBufSerialize;

public class NettyClientInitializer extends ChannelInitializer<SocketChannel>{
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		// 3s没有写数据触发事件
		ch.pipeline().addLast(new IdleStateHandler(0, 6, 0));
		
//		MessageCodecUtil util = new ProtoBufCodecUtil(new ProtoBufRpcSerialize(true));
		IMessageCodecUtil util = new ProtoBufCodecUtil(ProtoBufSerialize.getInstance(), true);
		
		
		ch.pipeline().addLast(new MessageEncoder(util));
		ch.pipeline().addLast(new MessageDecoder(util));
		
		ch.pipeline().addLast(new NettyClientHandler());
	}
}
