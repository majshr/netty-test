package multiple.ways.codec.codec;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 解码器(使用MessageCodecUtil)
 * @author bhz（maj）
 * @since 2019年8月20日
 */
public class MessageDecoder extends ByteToMessageDecoder{
	/**
	 * 编解码工具
	 */
	private IMessageCodecUtil util = null;
	
	public MessageDecoder(IMessageCodecUtil util) {
		super();
		this.util = util;
	}



	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		// 可读字节小于消息长度
		if(in.readableBytes() < IMessageCodecUtil.MESSAGE_LENGTH){
			return;
		}
		
		// 标记当前读的下标
		in.markReaderIndex();
		
		// 四个字节作为长度标记；读取信息长度
		int messageLength = in.readInt();
		if(messageLength < 0){
			ctx.close();
		}
		
		// 读取信息不到数据的长度，不能转换为对象，回退到之前读的状态，继续等待
		if(in.readableBytes() < messageLength){
			in.resetReaderIndex();
			return;
		} else{
			// 读取信息，转换为对象
			byte[] messageBody = new byte[messageLength];
			in.readBytes(messageBody);
			
			try{
				// 消息解码为对象
				Object obj = util.decode(messageBody);
				out.add(obj);
			} catch (IOException ex) {
                Logger.getLogger(MessageDecoder.class.getName()).log(Level.SEVERE, null, ex);
            }
		}
		
		
	}

}
