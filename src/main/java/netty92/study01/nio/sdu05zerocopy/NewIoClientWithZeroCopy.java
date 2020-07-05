package netty92.study01.nio.sdu05zerocopy;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
/**
 * 零拷贝, 将文件从FileChannel直接传到SocketChannel
 * @author bhz（maj）
 * @since 2020年7月3日
 */
public class NewIoClientWithZeroCopy {
	public static void main(String[] args) throws IOException {
		SocketChannel socketChannel = SocketChannel.open();
		SocketAddress socketAddress = new InetSocketAddress(8899);
		socketChannel.connect(socketAddress);
		socketChannel.configureBlocking(true);
		
		// 内存映射文件
		String filePath = "";
		FileChannel fileChannel = new FileInputStream(filePath).getChannel();
		
		long startTime = System.currentTimeMillis();
		
		// transfer比从一个channel循环读取，写到另一个channel效率更高
		/*
		 * Many operating systems can transfer bytes directly from the filesystem cache
		 * to the target channel without actually copying them.
		 */
		long transferCount = fileChannel.transferTo(0, fileChannel.size(), socketChannel);
		
		System.out.println("发送总字节数： " + transferCount + 
				"    时间：" + (System.currentTimeMillis() - startTime));
	}
}
