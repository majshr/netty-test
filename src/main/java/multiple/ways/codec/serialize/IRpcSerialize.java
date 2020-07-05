package multiple.ways.codec.serialize;

import java.io.InputStream;
import java.io.OutputStream;

public interface IRpcSerialize {
	/**
	 * 根据流信息转换为对象
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：</B><BR>
	 * @param input
	 * @return
	 */
    public Object deserialize(InputStream input) ;

    /**
     * 把对象转换为流信息
     * <B>方法名称：</B><BR>
     * <B>概要说明：</B><BR>
     * @param output
     * @param object
     */
    public void serialize(OutputStream output, Object object);
}
