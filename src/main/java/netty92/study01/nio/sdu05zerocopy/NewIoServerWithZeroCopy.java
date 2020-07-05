package netty92.study01.nio.sdu05zerocopy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NewIoServerWithZeroCopy {
	public static void main(String[] args) throws IOException {
		InetSocketAddress address = new InetSocketAddress(8899);
		
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		ServerSocket serverSocket = serverSocketChannel.socket();
		
		// 启用或禁用tcp选项
		// nable/disable the {@link SocketOptions#SO_REUSEADDR SO_REUSEADDR}
		/*
		 * tcp连接被关闭后，这个连接还会保持着一段时间的超时状态，TIME_WAIT状态
		 * 如果一个应用尝试绑定到处于超时状态的端口号，是不可能连接成功的，虽然连接关闭了，但仍处于超时状态。
		 * 设置启动这个参数，可以是应用能够绑定到端口号，即使有连接处于超时状态
		 * 要在bind方法之前调用
		 */
		serverSocket.setReuseAddress(true);
		
		serverSocket.bind(address);
		
		ByteBuffer buffer = ByteBuffer.allocate(4096);
		
		while(true) {
			// accept，非阻塞模式，直接返回空；阻塞模式，阻塞等待连接
			// accept返回的SocketChannel一定是阻塞模式的，可以自己修改为非阻塞的
			SocketChannel socketChannel = serverSocketChannel.accept();
			socketChannel.configureBlocking(true);
			
			int readCount = -1;
			long total = 0;
			while((readCount = socketChannel.read(buffer)) > 0) {
				total += readCount;
				buffer.clear();
			}
		}
	}
}
