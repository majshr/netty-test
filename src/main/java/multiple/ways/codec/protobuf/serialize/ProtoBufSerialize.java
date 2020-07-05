package multiple.ways.codec.protobuf.serialize;

import java.util.concurrent.ExecutionException;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import multiple.ways.codec.entity.Response;

/**
 * ProtoBufSerialize序列化和反序列化类
 * @author bhz（maj）
 * @since 2019年8月21日
 */
public class ProtoBufSerialize implements ISerialize{
	/** 单例 */
	private static ProtoBufSerialize protoBufSerialize = new ProtoBufSerialize();
	private ProtoBufSerialize(){
		
	}
	/** 单例 */
	public static ProtoBufSerialize getInstance(){
		return protoBufSerialize;
	}
	
	@Override
	public <T> byte[] serialize(T obj){
		Class<T> clazz = (Class<T>) obj.getClass();
		// 如果获取schema错误，直接返回
		Schema<T> schema;
		try {
			schema = SchemaCache.getSchema(clazz);
		} catch (ExecutionException e) {
			return null;
		}
		
		byte[] data = null;
		LinkedBuffer linkedBuffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
		try{
			data = ProtostuffIOUtil.toByteArray(obj, schema, linkedBuffer);
		} finally{
			linkedBuffer.clear();
		}
		return data;
	}
	
	@Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        Schema<T> schema;
		try {
			schema = SchemaCache.getSchema(clazz);
		} catch (ExecutionException e) {
			return null;
		}
		
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, obj, schema);
        return obj;
    }
	
	public static void main(String[] args) {
		Response response = new Response();
		response.setResult("hahaha");
		byte[] data = getInstance().serialize(response);
		
		System.out.println(getInstance().deserialize(data, Response.class).getResult());
	}
}
