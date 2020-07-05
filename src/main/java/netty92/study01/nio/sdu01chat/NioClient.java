package netty92.study01.nio.sdu01chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NioClient {
	public static void main(String[] args) throws IOException {
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		Selector selector = Selector.open();
		socketChannel.register(selector, SelectionKey.OP_CONNECT);
		socketChannel.connect(new InetSocketAddress("localhost", 8899));
		
		while(true) {
			selector.select();
			Set<SelectionKey> keys = selector.selectedKeys();
			for(SelectionKey key : keys) {
				if(key.isConnectable()) {
					SocketChannel clientChannel = (SocketChannel) key.channel();
					if(clientChannel.isConnectionPending()) {
						clientChannel.finishConnect();
						ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
						writeBuffer.put((LocalDateTime.now() + "连接成功").getBytes());
						writeBuffer.flip();
						clientChannel.write(writeBuffer);
						
						// 注册读感兴趣事件，才可以从服务器端读取到其他客户端发送的消息
						clientChannel.register(selector, SelectionKey.OP_READ);
						
						// 控制台接收数据，写数据
						ExecutorService exe = Executors.newSingleThreadExecutor();
						exe.submit(new Runnable() {
							
							@Override
							public void run() {
								while(true) {
									try {
										writeBuffer.clear();
										InputStreamReader input = new InputStreamReader(System.in);
										BufferedReader br = new BufferedReader(input);
										
										String sendMessage = br.readLine();
										
										writeBuffer.put(sendMessage.getBytes());
										writeBuffer.flip();
										clientChannel.write(writeBuffer);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
								
							}
						});
					}
				} else if(key.isReadable()) {
					SocketChannel clientChannel = (SocketChannel) key.channel();
					
					ByteBuffer readBuf = ByteBuffer.allocate(1024);
					int len = clientChannel.read(readBuf);
					String recMsg = null;
					if(len > 0) {
						recMsg = new String(readBuf.array(), 0, len);
					}
					System.out.println(recMsg);
				}
			}
			
			keys.clear();
		}
	}
}
