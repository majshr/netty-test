package v1.test05.netty2unpacking.fixedlength.characters;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
/**
 * 读取服务端响应的消息, 打印
 * @author bhz（maj）
 * @since 2020年7月2日
 */
public class ClientHandler extends ChannelInboundHandlerAdapter{
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			// 读取服务端返回的消息, 仅读取消息操作, 需要读取完成后, 清空缓冲区
			String response = (String) msg;
			System.out.println(response);
		} finally {
			ReferenceCountUtil.release(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}	
}
