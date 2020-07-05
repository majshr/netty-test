package v1.test06.netty3serializable.marshling;

import java.io.File;
import java.io.FileOutputStream;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import v1.test06.netty3serializable.util.GzipUtils;

public class ServerHandler extends ChannelInboundHandlerAdapter{

	
	/**
	 * 通道激活
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("server channel active... ");
	}


	/**
	 * 接收到客户端传来的数据
	 * @param ctx
	 * @param msg
	 * @throws Exception
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		/****获取请求信息*****/
		Request request = (Request) msg;
		System.out.println(request);
		// 接收文件
		// 文件数组
		byte[] data = GzipUtils.ungzip(request.getAttachment());
		File file = new File("C:\\Users\\maj\\Desktop\\netty_copy\\spring" + request.getId() + ".java");
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(data);
		
		/*****发送响应信息************/
		// 给客户端相应信息是异步的, 可以通过添加回调方法, 知道是否发送信息完成
		Response response = new Response();
		response.setId(request.getId());
		response.setName("resp" + request.getId());
		response.setResponseMessage("响应内容" + request.getId());
		ctx.writeAndFlush(response);
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx)
			throws Exception {
		
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable t)
			throws Exception {
		ctx.close();
	}
	
}
































