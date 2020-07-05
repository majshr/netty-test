package netty92.study01.nio.sdu02chrset;

import java.nio.charset.Charset;

public class PrintCharsetsTest {
	public static void main(String[] args) {
		// 打印计算机支持的字符集
		Charset.availableCharsets().forEach((k, v) -> {
			System.out.println(k + " = " + v);
		});
	}
}





















