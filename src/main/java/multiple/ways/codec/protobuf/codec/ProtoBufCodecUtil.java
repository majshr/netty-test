package multiple.ways.codec.protobuf.codec;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import multiple.ways.codec.codec.IMessageCodecUtil;
import multiple.ways.codec.entity.Request;
import multiple.ways.codec.entity.Response;
import multiple.ways.codec.protobuf.serialize.ISerialize;
/**
 * protoBuf方式实现编解码工具
 * @author bhz（maj）
 * @since 2020年7月4日
 */
public class ProtoBufCodecUtil implements IMessageCodecUtil{

	private ISerialize serialize;
	boolean rpcDirect = false; 
	boolean cliented = false;
	
//	public ProtoBufCodecUtil(RpcSerialize rpcSerialize) {
//		super();
//		this.rpcSerialize = rpcSerialize;
//	}
	
	public ProtoBufCodecUtil(ISerialize serialize, boolean cliented) {
		super();
		this.serialize = serialize;
		this.cliented = cliented;
	}

	@Override
	public void encode(ByteBuf out, Object message) throws IOException {
		/*try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();){
			rpcSerialize.serialize(byteArrayOutputStream, message);
			byte[] body = byteArrayOutputStream.toByteArray();
			int dataLength = body.length;
			out.writeInt(dataLength);
			out.writeBytes(body);
		}*/
		byte[] body = serialize.serialize(message);
		out.writeInt(body.length);
		out.writeBytes(body);
	}

	@Override
	public Object decode(byte[] body) throws IOException {
		/*try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);){
			return rpcSerialize.deserialize(byteArrayInputStream);
		}*/
		
		// 根据客户端还是服务端, 编解码为相应对象
		Class clazz = cliented ? Response.class : Request.class;
		return serialize.deserialize(body, clazz);
	}

}
