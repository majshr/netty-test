package netty92.study01.nio.sdu01nioserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Nio03SocketServer {
	public static void main(String[] args) throws IOException {
		int[] ports = new int[5];
		ports[0] = 5000;
		ports[1] = 5001;
		ports[2] = 5002;
		ports[3] = 5003;
		ports[4] = 5004;
		
		// 创建选择器
		Selector selector = Selector.open();
		
		for(int i = 0; i < ports.length; i++){
			// 创建服务端Channel，绑定端口号
			ServerSocketChannel channel = ServerSocketChannel.open();
			channel.configureBlocking(false);
			ServerSocket serverSocket = channel.socket();
			InetSocketAddress address = new InetSocketAddress(ports[i]);
			serverSocket.bind(address);
			
			// 服务端Channel注册到Selector(初始必须注册为accept)
			SelectionKey key = channel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("监听端口：" + ports[i]);
		}
		
		while(true){
			// 已经就绪的通道的数量，一直阻塞到有就绪的通道
			int keyNums = selector.select();
			System.out.println("keyNums:" + keyNums);
			
			// 获取就绪的通道
			Set<SelectionKey> selectKeySets = selector.selectedKeys();
			Iterator<SelectionKey> selectKeyIte = selectKeySets.iterator();
			
			// 处理
			while(selectKeyIte.hasNext()){
				SelectionKey selectionKey = selectKeyIte.next();
				
				// 如果就绪时间为接收连接
				if(selectionKey.isAcceptable()){
					/**第一次连接，需要服务端accept*/
					// 可以根据key获取服务端Channel
					ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
					// 服务端接收连接，获取连接通道SocketChannel
					SocketChannel socketChannel = serverSocketChannel.accept();
					// SocketChannel注册为非阻塞
					socketChannel.configureBlocking(false);
					
					socketChannel.register(selector, SelectionKey.OP_READ);
					
					// 必须要移除，否则还会监听这个事件，但连接已经建立了
					selectKeyIte.remove();
					
					System.out.println("获取到客户端连接：" + socketChannel);
				} else if(selectionKey.isReadable()){
					// 已经建立连接，不需要重新accept了，直接可以获取SocketChannel
					SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
					
					ByteBuffer buffer = ByteBuffer.allocate(1024);
					int len = socketChannel.read(buffer);
					int byteRead = 0;
					while(len > 0){
						byteRead += len;
						buffer.flip();
						// 信息会写给客户端
						socketChannel.write(buffer);
						
						// 再读取下一次的数据
						buffer.clear();
						len = socketChannel.read(buffer);
					}
					
					System.out.println("读取" + byteRead + "字节数据，数据来自于：" + socketChannel);
					
					selectKeyIte.remove();
				}
				
			}
		}
		
	}
}





























