package v1.test08.netty6heartBeat.custom;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * 有心跳检测的功能(定时发送心跳请求)
 * @author bhz（maj）
 * @since 2020年7月2日
 */
public class ClientHandler extends ChannelInboundHandlerAdapter{
	
	private static final String SUCCESS_KEY = "auth_success_key";
	
	/**
	 * 定时任务
	 */
	private static final ScheduledExecutorService schedule = 
			Executors.newScheduledThreadPool(1);
	
	private String ip;
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// 通道激活后, client先向服务端发送认证信息
		// 认证信息为ip_ip机器对应的认证key
		ip = InetAddress.getLocalHost().getHostAddress();
		String key = "123456";
		
		// 拼接认证信息, 发送给服务端
		String auth = ip + "," + key;
		ctx.writeAndFlush(auth);
	}
	
	public static void main(String[] args) throws UnknownHostException {
		System.out.println(InetAddress.getLocalHost().getHostAddress());
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			if(msg instanceof String){
				// 认证是否通过信息
				String authRes = (String) msg;
				// 如果认证成功, 定时发送心跳
				if(SUCCESS_KEY.equals(authRes)){
					// 2s之后执行一次, 之后每3s执行一次
					schedule.scheduleWithFixedDelay(new HeartBeatTask(ctx), 
							2, 3, TimeUnit.SECONDS);
				}else{
					// 认证失败, 打印认证信息, 连接被关闭
					System.out.println(msg);
				}
			}
		} finally {
			ReferenceCountUtil.release(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// 关闭线程池
		schedule.shutdown();
		cause.printStackTrace();
		ctx.close();
	}	
	
	/**
	 * 定时心跳发送任务
	 * @author maj
	 *
	 */
	private class HeartBeatTask implements Runnable{

		private ChannelHandlerContext ctx;
		
		public HeartBeatTask(ChannelHandlerContext ctx) {
			this.ctx = ctx;
		}
		
		@Override
		public void run() {
			try {
				Request request = new Request();
				//ip
				request.setIp(ip);
				Sigar sigar = new Sigar();
				//cpu prec
				CpuPerc cpuPerc = sigar.getCpuPerc();
				HashMap<String, Object> cpuPercMap = new HashMap<String, Object>();
				cpuPercMap.put("combined", cpuPerc.getCombined());
				cpuPercMap.put("user", cpuPerc.getUser());
				cpuPercMap.put("sys", cpuPerc.getSys());
				cpuPercMap.put("wait", cpuPerc.getWait());
				cpuPercMap.put("idle", cpuPerc.getIdle());
				// memory
				Mem mem = sigar.getMem();
				HashMap<String, Object> memoryMap = new HashMap<String, Object>();
				memoryMap.put("total", mem.getTotal() / 1024L);
				memoryMap.put("used", mem.getUsed() / 1024L);
				memoryMap.put("free", mem.getFree() / 1024L);
				request.setCpuPercMap(cpuPercMap);
				request.setMemoryMap(memoryMap);
				
				// 发送请求信息
				ctx.writeAndFlush(request);
			} catch (SigarException e) {
				e.printStackTrace();
			}
		}
		
	}
}
