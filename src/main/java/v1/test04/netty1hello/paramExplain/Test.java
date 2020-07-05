package v1.test04.netty1hello.paramExplain;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Test {
	public static void main(String[] args) throws UnknownHostException, IOException {
		Socket socket = new Socket("localhost", 8765);
		OutputStream os = socket.getOutputStream();
		PrintWriter pw =new PrintWriter(os);
		pw.write("用户名：admin；密码：123");
		pw.flush();
		socket.shutdownOutput();
		System.out.println("连接成功!");
	}
}

