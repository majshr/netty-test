package netty92.study05.codec.unpackingAndSticking.resolve;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import netty92.study05.codec.unpackingAndSticking.resolve.codec.MyPersonDecoder;
import netty92.study05.codec.unpackingAndSticking.resolve.codec.MyPersonEncoder;

public class MyClient {
	public static void main(String[] args) throws InterruptedException {
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
			.handler(new ChannelInitializer<Channel>() {

				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast(new MyPersonDecoder());
					ch.pipeline().addLast(new MyPersonEncoder());
					ch.pipeline().addLast(new MyClientHandler());
				}
			});
			
			ChannelFuture channelFuture = bootstrap.connect("localhost", 8899).sync();
			channelFuture.channel().closeFuture().sync();
		}finally {
			eventLoopGroup.shutdownGracefully();
		}
	}
}
