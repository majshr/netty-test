package v1.test08.netty6heartBeat.custom;

import java.util.HashMap;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter{

	/** key:ip value:auth */
	private static HashMap<String, String> AUTH_IP_MAP = new HashMap<String, String>();
	private static final String SUCCESS_KEY = "auth_success_key";
	
	static {
		AUTH_IP_MAP.put("192.168.31.21", "123456");
	}
	
	/**
	 * 验证客户端发过来的验证信息是否正确
	 * @param ctx
	 * @param auth
	 * @return {@link Boolean}
	 */
	private boolean auth(ChannelHandlerContext ctx, String auth){
		// 收到slave认证信息
		if(auth.contains(",")){
			String[] strs = auth.split(",");
			String ip = strs[0];
			String key = strs[1];
			
			
			if(key.equals(AUTH_IP_MAP.get(ip))){
				// 验证通过, 向客户端发送验证通过信息
				ctx.writeAndFlush(SUCCESS_KEY);
				return true;
			}	
		}
		
		// 验证失败, 通知客户端失败, 并关闭连接
		ctx.writeAndFlush("auth failure! ").addListener(ChannelFutureListener.CLOSE);
		return false;
	}
	
	/**
	 * 接收到客户端传来的数据
	 * @param ctx
	 * @param msg
	 * @throws Exception
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof String){
			// 认证信息
			auth(ctx, (String)msg);
		} else if(msg instanceof Request){
			// 心跳信息
			Request info = (Request) msg;
			System.out.println("--------------------------------------------");
			System.out.println("当前主机ip为: " + info.getIp());
			System.out.println("当前主机cpu情况: ");
			HashMap<String, Object> cpu = info.getCpuPercMap();
			System.out.println("总使用率: " + cpu.get("combined"));
			System.out.println("用户使用率: " + cpu.get("user"));
			System.out.println("系统使用率: " + cpu.get("sys"));
			System.out.println("等待率: " + cpu.get("wait"));
			System.out.println("空闲率: " + cpu.get("idle"));
			
			System.out.println("当前主机memory情况: ");
			HashMap<String, Object> memory = info.getMemoryMap();
			System.out.println("内存总量: " + memory.get("total"));
			System.out.println("当前内存使用量: " + memory.get("used"));
			System.out.println("当前内存剩余量: " + memory.get("free"));
			System.out.println("--------------------------------------------");
			
			ctx.writeAndFlush("info received!");
		} else{
			//  请求错误, 关闭连接
			ctx.writeAndFlush("connect failure!").addListener(ChannelFutureListener.CLOSE);
		}
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
































