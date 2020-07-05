package multiple.ways.codec.protobuf.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {
	private static final int port = 9876; // 设置服务端端口
    private static EventLoopGroup boss = new NioEventLoopGroup(); // 通过nio方式来接收连接和处理连接
    private static EventLoopGroup work = new NioEventLoopGroup(); // 通过nio方式来接收连接和处理连接
    private static ServerBootstrap b = new ServerBootstrap();
    
    public static void run(){
    	try{
    		b.group(boss, work).channel(NioServerSocketChannel.class)
    		.childHandler(new NettyServerInitializer());
    		
    		ChannelFuture future = b.bind("localhost", port).sync();
    		future.channel().closeFuture().sync();
    		
    	} catch(Exception e){
    		
    	}finally{
    		// 关闭EventLoopGroup，释放掉所有资源包括创建的线程
            work.shutdownGracefully();
            boss.shutdownGracefully();
    	}
    }
    
    public static void main(String[] args) {
		run();
	}
}
























