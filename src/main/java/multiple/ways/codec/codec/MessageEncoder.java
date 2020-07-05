package multiple.ways.codec.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<Object>{

	private IMessageCodecUtil util = null;
	
	public IMessageCodecUtil getUtil() {
		return util;
	}

	public void setUtil(IMessageCodecUtil util) {
		this.util = util;
	}

	public MessageEncoder(IMessageCodecUtil util) {
		super();
		this.util = util;
	}

	/**
	 * 把对象编码到ByteBuf中
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：</B><BR>
	 * @see io.netty.handler.codec.MessageToByteEncoder#encode(io.netty.channel.ChannelHandlerContext, java.lang.Object, io.netty.buffer.ByteBuf)
	 */
	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		util.encode(out, msg);
	}

}
