package netty92.study06.chatroom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class MyChatClient {
	public static void main(String[] args) throws InterruptedException, IOException {
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
						// 分隔符解码器（行分隔符）
						ch.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()));
						ch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
						ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));
						ch.pipeline().addLast(new MyChatClientHandler());
					}
				}); 
			ChannelFuture channelFuture = bootstrap.connect("localhost", 8899).sync();
			Channel channel = channelFuture.channel();
			
			// 阻塞读取控制台输入信息，发送给服务端，再由服务端发送给相应的客户对象
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			for(;;){
				channel.writeAndFlush(br.readLine() + "\r\n");
			}
			
			
//			channelFuture.channel().closeFuture().sync();
		}finally{
			loopGroup.shutdownGracefully();
		}
		
	}
}


















