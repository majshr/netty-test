package multiple.ways.codec.protobuf.serialize;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
/**
 * Schema缓冲池
 * @author bhz（maj）
 * @since 2020年7月4日
 */
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
	public static <T> Schema<T> getSchema(Class<T> clazz) throws ExecutionException{
		return (Schema<T>) cache.get(clazz);
	}
	
//	public static ThreadLocal<LinkedBuffer> buffer = new ThreadLocal<LinkedBuffer>(){
//		protected LinkedBuffer initialValue() {
//			return LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
//		}
//	};
	
}


















