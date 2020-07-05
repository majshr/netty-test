package netty92.study01.nio.sdu01chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

public class NioServer {
	private static Map<String, SocketChannel> clientMap = new HashMap<String, SocketChannel>();
	
	public static void main(String[] args) throws IOException {
		
		ServerSocketChannel  serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		ServerSocket serverSocket = serverSocketChannel.socket();
		serverSocket.bind(new InetSocketAddress(8899));
		
		Selector selector = Selector.open();
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		
		while(true) {
			try {
				// 返回关注的事件的数量
				int num = selector.select();
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				selectionKeys.forEach(selectionKey -> {
					if(selectionKey.isAcceptable()) {
						try {
							ServerSocketChannel serverSocChannel = (ServerSocketChannel) selectionKey.channel();
							SocketChannel socketChannel = serverSocChannel.accept();
							socketChannel.configureBlocking(false);
							socketChannel.register(selector, SelectionKey.OP_READ);
							
							String key = "【" + UUID.randomUUID().toString() + "】";
							clientMap.put(key, socketChannel);
							
						} catch (ClosedChannelException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else if(selectionKey.isReadable()) {
						try {
							SocketChannel clientChannel = (SocketChannel) selectionKey.channel();
							ByteBuffer readBuffer = ByteBuffer.allocate(1024);
							
							int count = clientChannel.read(readBuffer);
							if(count > 0) {
								readBuffer.flip();
								Charset charset = Charset.forName("utf-8");
								String recMsg = String.valueOf(charset.decode(readBuffer).array());
								System.out.println(clientChannel + ": " + recMsg);
								
								// 将消息分发给所有客户端
								String senderKey = null;
								for(Entry<String, SocketChannel> entry : clientMap.entrySet()) {
									if(clientChannel == entry.getValue()) {
										senderKey = entry.getKey();
										break;
									}
								}
								
								for(Entry<String, SocketChannel> entry : clientMap.entrySet()) {
									SocketChannel value = entry.getValue();
									
									ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
									String sendMsg = senderKey + ": " + recMsg;
									writeBuffer.put(sendMsg.getBytes());
									writeBuffer.flip();
									System.out.println("广播消息：" + sendMsg);
									value.write(writeBuffer);
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
				
				selectionKeys.clear();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
