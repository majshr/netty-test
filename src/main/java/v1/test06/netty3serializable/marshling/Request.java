package v1.test06.netty3serializable.marshling;

import java.io.Serializable;

/**
 * 序列化一定要实现Serializable接口 如果是两个netty项目程序相互通信, Req和Resp在两个项目必须都存在,
 * 且包路径必须相同, 否则序列化不能成功
 */
public class Request implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id ;
	private String name ;
	private String requestMessage ;
	
	/**
	 * 文件信息, 读取文件信息, 压缩, 形成二进制数组
	 */
	private byte[] attachment;
	
	@Override
	public String toString() {
		return "[ " + id + "    " + name + "    " +  requestMessage + " ]";
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRequestMessage() {
		return requestMessage;
	}
	public void setRequestMessage(String requestMessage) {
		this.requestMessage = requestMessage;
	}
	public byte[] getAttachment() {
		return attachment;
	}
	public void setAttachment(byte[] attachment) {
		this.attachment = attachment;
	}
}
