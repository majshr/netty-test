package netty92.study01.nio.sdu02chrset;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
/**
 * 
 * @author bhz（maj）
 * @since 2020年7月2日
 */
public class EncodeDecodeCharsetTest {
	public static void main(String[] args) throws IOException {
		charSetTest1();
	}
	
	private static void charSetTest1() throws IOException{
		String inputFile = "";
		String outputFile = "";
		
		// 读, 写文件Channel
		RandomAccessFile inputReaderAccessFile = new RandomAccessFile(inputFile, "r");
		RandomAccessFile outputWriterAccessFile = new RandomAccessFile(outputFile, "rw");
		FileChannel inChannel = inputReaderAccessFile.getChannel();
		FileChannel outChannel = outputWriterAccessFile.getChannel();
		
		// 内存映射
		MappedByteBuffer mappedByteBuffer = inChannel.map(FileChannel.MapMode.READ_WRITE, 0, inputReaderAccessFile.length());
		
		// 生成编解码器
		// 解码：将文件内容解析成字符串； 编码：转换成字符串
		Charset charset = Charset.forName("utf-8");
		CharsetDecoder decoder = charset.newDecoder();
		CharsetEncoder encoder = charset.newEncoder();
		
		/**
		 * 每个磁盘上的文件都会有一个编码
		 */
		// 使用同一种编码方式, 解码码再编码, 还会回到原来的字节流
		// 解码
		CharBuffer charBuffer = decoder.decode(mappedByteBuffer);
		// 编码
		ByteBuffer outputData = encoder.encode(charBuffer);
		
		// 写到out文件
		outChannel.write(outputData);
		
		inputReaderAccessFile.close();
		outputWriterAccessFile.close();
	}
}































