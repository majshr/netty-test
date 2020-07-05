package v1.test06.netty3serializable.marshling;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import v1.test06.netty3serializable.util.GzipUtils;

public class Client {
	private int port;
	public Client(int port) {
		this.port = port;
	}
	public void start() throws InterruptedException, IOException{
		// 客户端仅发送请求, 不需要处理请求连接的线程组
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(group)
		.channel(NioSocketChannel.class)
		.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel sc) throws Exception {
				// 设置marshalling编解码器
				sc.pipeline().addLast(MarshallingCodeFactory.buildMarshallingEncoder())
				.addLast(MarshallingCodeFactory.buildMarshllingDecoder());
				
				sc.pipeline().addLast(new ClientHandler());
			}
		});
		
		// 连接服务端
		ChannelFuture cf = b.connect("127.0.0.1", port).sync();
		
		// 发送信息
		for(int i = 0; i < 5; i++){
			Request request = new Request();
			request.setId("" + i);
			request.setName("pro" + i);
			request.setRequestMessage("数据信息" + i);
			
			// 设置文件信息
			FileInputStream fileInputStream = 
					new FileInputStream(new File("C:\\Users\\maj\\Desktop\\spring.java"));
			// 读取文件信息到二进制流
			byte[] data = new byte[fileInputStream.available()];
			fileInputStream.read(data);
			
			// 将压缩数据放进请求
			request.setAttachment(GzipUtils.gzip(data));
			
			cf.channel().writeAndFlush(request);
		}
		
		cf.channel().closeFuture().sync();
		group.shutdownGracefully();
	}
	
	public static void main(String[] args) throws Exception{
		new Client(8765).start();
	}
}
