package netty92.study01.nio.sdu04scattering_gathering;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
/**
 * 读取消息到缓冲区数组
 * @author bhz（maj）
 * @since 2020年7月3日
 */
public class ScatGatherTest {
	public static void main(String[] args) throws IOException {
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		InetSocketAddress address = new InetSocketAddress(8899);
		serverSocketChannel.socket().bind(address);

		// 消息长度为三个Buffer总和
		int messageLength = 2 + 3 + 4;
		ByteBuffer[] buffers = new ByteBuffer[3];
		buffers[0] = ByteBuffer.allocate(2);
		buffers[1] = ByteBuffer.allocate(3);
		buffers[2] = ByteBuffer.allocate(4);
		
		SocketChannel socketChannel = serverSocketChannel.accept();
		
		while(true) {
			int bytesRead = 0;
			while(bytesRead < messageLength) {
				// 信息读取到buffer
				long r = socketChannel.read(buffers);
				bytesRead += r;
				
				System.out.println("bytesRead: " + bytesRead);
				Arrays.asList(buffers).forEach(buffer->{
					System.out.println("position: " + buffer.position() + ", limit: " + buffer.limit());
				});
			}
			
			// 读取完一次，需要读取缓冲内容，flip一次
			Arrays.asList(buffers).forEach(buffer->{
				buffer.flip();
			});
			
			// 读取到信息后，再响应回去
			long bytesWrite = 0L;
			while(bytesWrite < messageLength) {
				long r = socketChannel.write(buffers);
				bytesWrite += r;
			}
			
			Arrays.asList(buffers).forEach(buffer->{
				buffer.clear();
			});
			
			System.out.println(bytesRead + "----" + bytesWrite);
		}
	}
}
