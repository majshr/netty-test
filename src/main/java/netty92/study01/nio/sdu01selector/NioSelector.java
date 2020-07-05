package netty92.study01.nio.sdu01selector;

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
/**
 * 一个selector管理多个端口服务
 * @author maj
 *
 */
public class NioSelector {
	public static void main(String[] args) throws IOException {
		int[] ports = new int[5];
		
		ports[0] = 5000;
		ports[1] = 5001;
		ports[2] = 5002;
		ports[3] = 5003;
		ports[4] = 5004;
		
		Selector selector = Selector.open();
		
		for(int i = 0; i < ports.length; i++) {
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			ServerSocket serverSocket = serverSocketChannel.socket();
			
			InetSocketAddress address = new InetSocketAddress(ports[i]);
			serverSocket.bind(address);
			
			// Channel注册到selector
			// 感兴趣的事件为建立连接
			// 每个Channel被注册到selector时，都会创建一个SelectionKey
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			
			System.out.println("监听端口: " + ports[i]);
			
		}
		
		while(true) {
			int numbers = selector.select();
			System.out.println("numbers: " + numbers);
			Set<SelectionKey> selectionKeys = selector.selectedKeys();
			Iterator<SelectionKey> iter = selectionKeys.iterator();
			while(iter.hasNext()) {
				SelectionKey selectionKey = iter.next();
				
				if(selectionKey.isAcceptable()) {
					ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
					// 获取客户端连接channel，注册到selector
					SocketChannel socketChannel = serverSocketChannel.accept();
					socketChannel.configureBlocking(false);
					socketChannel.register(selector, SelectionKey.OP_READ);
					iter.remove();
					
					System.out.println("获取到客户端连接。" + socketChannel);
				} else if(selectionKey.isReadable()) {
					SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
					ByteBuffer buffer = ByteBuffer.allocate(1024);
					
					int bytesRead = 0;
					int len = 0;
					while((len = socketChannel.read(buffer)) > 0) {
						bytesRead += len;
						
						buffer.flip();
						socketChannel.write(buffer);
						
						buffer.clear();
					}
					
					System.out.println("共接收数据：" + bytesRead);
					iter.remove();
				}
				
			}
		}
	}
	
}
















