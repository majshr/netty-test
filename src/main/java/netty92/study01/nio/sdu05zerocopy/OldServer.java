package netty92.study01.nio.sdu05zerocopy;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class OldServer {
	public static void main(String[] args) throws Exception {
		ServerSocket serverSocket = new ServerSocket(8899);
		
		while(true) {
			Socket socket = serverSocket.accept();
			// 二进制流
			DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
			
			byte[] byteArray = new byte[4096];
			while(true) {
				// 最多读去byteArray.length个字节；
				// 一直阻塞，知道数据达到了数量，探测到结束，或异常被抛出
				// 流信息被读完，返回-1
				int readCount = dataInputStream.read(byteArray, 0, byteArray.length);
				
				if(readCount == -1) {
					break;
				}
			}
		}
	}
}
