package example.sdu01.multipleConnect;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

public class MultiConnectionTest {
	// 创建多个连接时，EventLoopGroup可以重用
	public static void main(String[] args) throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(group).channel(NioSocketChannel.class)
		.option(ChannelOption.TCP_NODELAY, true)
		.handler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new LoggingHandler());
			}
		});
		
		// 创建100次连接
		int connectSize = 100;
		String host = "localhost";
		int port = 8080;
		// 多次连接，使用一个启动器即可（所有客户端使用此创建连接）
		/***************************************/
		/**
		 * connect可以并发执行
		 * 尽管Bootstrap自身不是线程安全的，但执行Bootstrap的连接操作是串行执行的，且connect方法本身是线程安全的
		 * 他会创建一个新的NioSocketChannel，并从初始构造的EventLoopGroup中选择一个NioEventLoop线程执行
		 * 真正的Channel连接操作
		 * 与执行Bootstrap的线程无关，所以通过一个Bootstrap连续发起多个连接操作是安全的。
		 */
		/**
		 * ***************释放资源*****************************
		 * 如果某个链路发生异常或关闭时，只需要释放Channel本身即可，不能同时销毁Channel所使用的NioEventLoop
		 * 和所在线程组
		 */
		for(int i = 0; i < connectSize; i++){
			b.connect(host, port).sync();
		}
	}
	
}
