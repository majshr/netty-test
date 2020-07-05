package v1.test08.netty6heartBeat.custom;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 序列化一定要实现Serializable接口 如果是两个netty项目程序相互通信, Req和Resp在两个项目必须都存在,
 * 且包路径必须相同, 否则序列化不能成功
 */
public class Request implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ip ;
	/**
	 * cpu信息
	 */
	private HashMap<String, Object> cpuPercMap ;
	/**
	 * 内存信息
	 */
	private HashMap<String, Object> memoryMap;
	@Override
	public String toString() {
		return "[ " + ip + "  ,  " + cpuPercMap + "  ,  " +  memoryMap + " ]";
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public HashMap<String, Object> getCpuPercMap() {
		return cpuPercMap;
	}

	public void setCpuPercMap(HashMap<String, Object> cpuPercMap) {
		this.cpuPercMap = cpuPercMap;
	}

	public HashMap<String, Object> getMemoryMap() {
		return memoryMap;
	}

	public void setMemoryMap(HashMap<String, Object> memoryMap) {
		this.memoryMap = memoryMap;
	}

}
