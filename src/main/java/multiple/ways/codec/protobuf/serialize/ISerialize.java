package multiple.ways.codec.protobuf.serialize;

import java.util.concurrent.ExecutionException;

import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;

public interface ISerialize {
	/**
	 * 序列化对象
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：</B><BR>
	 * @param obj
	 * @return
	 */
	<T> byte[] serialize(T obj);
	
	/**
	 * 反序列化
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：</B><BR>
	 * @param data
	 * @param clazz
	 * @return
	 */
    <T> T deserialize(byte[] data, Class<T> clazz);

}
