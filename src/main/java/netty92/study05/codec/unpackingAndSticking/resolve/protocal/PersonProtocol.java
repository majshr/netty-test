package netty92.study05.codec.unpackingAndSticking.resolve.protocal;

/**
 * Person对象协议
 * @author bhz（maj）
 * @since 2020年7月4日
 */
public class PersonProtocol {
	/**
	 * 数据长度
	 */
	private int length;
	/**
	 * 数据内容
	 */
	private byte[] content;

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}
}
