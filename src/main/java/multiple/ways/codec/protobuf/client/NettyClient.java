package multiple.ways.codec.protobuf.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
	private static final int port = 9876; // 设置连接的服务端端口
    private static EventLoopGroup work = new NioEventLoopGroup(); // 通过nio方式来接收连接和处理连接
    private static Bootstrap b = new Bootstrap();
    public static void run(Bootstrap b, EventLoopGroup work){
    	try{
    		b.group(work).channel(NioSocketChannel.class)
    		.handler(new NettyClientInitializer());
    		
    		ChannelFuture future = b.connect("localhost", port).sync();
    		future.channel().closeFuture().sync();
    		
    	} catch(Exception e){
    		
    	}finally{
    		// 关闭EventLoopGroup，释放掉所有资源包括创建的线程
//            work.shutdownGracefully();
    	}
    }
    
    public static void main(String[] args) {
		run(b, work);
	}
}














