package example.sdu03.chatroom.websocket;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ConcurrentPerformanceServerHandler extends ChannelInboundHandlerAdapter{
	AtomicInteger counter = new AtomicInteger(0);
	static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// 定时任务线程池周期统计服务端的处理能力
		scheduledExecutorService.scheduleAtFixedRate(() -> {
			int qps = counter.getAndSet(0);
			System.out.println("The server qps is: " + qps);
		}, 0, 1000, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		((ByteBuf)msg).release();
		counter.incrementAndGet();
		
		// 业务逻辑处理，模拟业务访问DB、缓存等，延时在100ms-1000ms之间
		Random random = new Random();
		try{
			TimeUnit.MICROSECONDS.sleep(random.nextInt(1000));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
