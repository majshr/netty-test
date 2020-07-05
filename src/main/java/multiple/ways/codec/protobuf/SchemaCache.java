package multiple.ways.codec.protobuf;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public class SchemaCache {
	static CacheLoader<Class<?>, Schema<?>> loader = new CacheLoader<Class<?>, Schema<?>>(){

		@Override
		public Schema<?> load(Class<?> key) throws Exception {
			return RuntimeSchema.getSchema(key);
		}
		
	};
	
	/**
	 * 1小时没有访问，销毁
	 */
	static LoadingCache<Class<?>, Schema<?>> cache = CacheBuilder.newBuilder()
			.expireAfterAccess(1, TimeUnit.HOURS)
			.build(loader);
	
	/**
	 * 获取Schema对象
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：</B><BR>
	 * @param clazz
	 * @return
	 * @throws ExecutionException
	 */
	private static <T> Schema<T> getSchema(Class<T> clazz) throws ExecutionException{
		return (Schema<T>) cache.get(clazz);
	}
	
	static ThreadLocal<LinkedBuffer> buffer = new ThreadLocal<LinkedBuffer>(){
		protected LinkedBuffer initialValue() {
			return LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
		}
	};
	
	/**
	 * 序列化对象
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：</B><BR>
	 * @param obj
	 * @return
	 */
	public static <T> byte[] serialize(T obj){
		Class<T> clazz = (Class<T>) obj.getClass();
		// 如果获取schema错误，直接返回
		Schema<T> schema;
		try {
			schema = getSchema(clazz);
		} catch (ExecutionException e) {
			return null;
		}
		
		byte[] data = null;
		try{
			data = ProtostuffIOUtil.toByteArray(obj, schema, buffer.get());
		} finally{
			buffer.remove();
		}
		return data;
	}
	
	/**
	 * 反序列化
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：</B><BR>
	 * @param data
	 * @param clazz
	 * @return
	 */
    public static <T> T deserialize(byte[] data, Class<T> clazz) {
        Schema<T> schema;
		try {
			schema = getSchema(clazz);
		} catch (ExecutionException e) {
			return null;
		}
		
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, obj, schema);
        return obj;
    }
}


















