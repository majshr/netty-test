package multiple.ways.codec.protobuf.rpcserialize;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import multiple.ways.codec.entity.Request;
import multiple.ways.codec.entity.Response;
import multiple.ways.codec.protobuf.serialize.SchemaCache;
import multiple.ways.codec.serialize.IRpcSerialize;

public class ProtoBufRpcSerialize implements IRpcSerialize{

    private static Objenesis objenesis = new ObjenesisStd(true);
    /**
     * true：客户端；  false：服务端
     */
    private boolean client = false;
    
	public ProtoBufRpcSerialize(boolean client) {
		super();
		this.client = client;
	}

	@Override
	public Object deserialize(InputStream input) {
		// 将流信息转换为对象；服务区端转化为请求，客户端转为为响应
		Class cls = client ? Response.class : Request.class;
		Object message = objenesis.newInstance(cls);
		try {
			Schema<Object> schema = SchemaCache.getSchema(cls);
			ProtobufIOUtil.mergeFrom(input, message, schema);
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return message;
	}

	@Override
	public void serialize(OutputStream output, Object object) {
		Class cls = (Class) object.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema schema = SchemaCache.getSchema(cls);
            ProtobufIOUtil.writeTo(output, object, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
	}

}
