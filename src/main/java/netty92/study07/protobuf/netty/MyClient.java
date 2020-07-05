package netty92.study07.protobuf.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import netty92.study07.protobuf.entity.MyDataInfo;

public class MyClient {
	public static void main(String[] args) throws InterruptedException {
		// 客户端只需要一个处理连接的线程池
		NioEventLoopGroup loopGroup = new NioEventLoopGroup();
		
		try{
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(loopGroup)
				.channel(NioSocketChannel.class) 
				// 客户端一般只使用handler，服务端可能使用handler也可能childHandler
				//handler针对的是bossGroup，childHandler针对workerGroup，处理连接具体操作
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
						// 解码成的对象
						ch.pipeline().addLast(new ProtobufDecoder(MyDataInfo.MyMessage.getDefaultInstance()));
						ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
						ch.pipeline().addLast(new ProtobufEncoder());
						ch.pipeline().addLast(new MyClientHandler());
					}
				}); 
			ChannelFuture channelFuture = bootstrap.connect("localhost", 8899).sync();
			
			channelFuture.channel().closeFuture().sync();
		}finally{
			loopGroup.shutdownGracefully();
		}
		
	}
}
















