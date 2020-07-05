package multiple.ways.codec.codec;

import java.io.IOException;

import io.netty.buffer.ByteBuf;

/**
 * 编解码工具接口；
 * 可以根据不同编码类型，实现不同，如Protobuf，java序列化，json序列化等
 * @author bhz（maj）
 * @since 2019年8月21日
 */
public interface IMessageCodecUtil {
	/**
	 * 消息长度信息, 使用四个字节(一个int长度)表示消息长度
	 */
	final static int MESSAGE_LENGTH = 4;
	
	/**
	 * 编码message对象编码到out字节数字
	 * @param out
	 * @param message
	 * @throws IOException
	 */
	void encode(final ByteBuf out, final Object message) throws IOException;
	
	/**
	 * 解码, 字节数组解码成对象
	 * @param body
	 * @return
	 * @throws IOException
	 */
	Object decode(byte[] body) throws IOException; 
}
