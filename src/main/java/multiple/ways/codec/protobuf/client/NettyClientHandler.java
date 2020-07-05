package multiple.ways.codec.protobuf.client;

import java.util.Date;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import multiple.ways.codec.entity.Request;
import multiple.ways.codec.entity.Response;

public class NettyClientHandler extends SimpleChannelInboundHandler<Response>{

	private int fcount = 1;
	
    /**
     * 建立连接时
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("建立连接时间：" + new Date());
        ctx.fireChannelActive();
    }
	
    /**
     * 关闭连接时
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("关闭连接时间：" + new Date());
        final EventLoop eventLoop = ctx.channel().eventLoop();
        // 重新连接
        NettyClient.run(new Bootstrap(), eventLoop);
        super.channelInactive(ctx);
    }
    
    /**
     * 心跳请求处理 每3秒发送一次心跳请求;
     * 
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object obj) throws Exception {
        System.out.println("循环请求的时间：" + new Date() + "，次数" + fcount);
        if (obj instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) obj;
            // 如果间隔时间内没有发送写指令，发送请求
            if (IdleState.WRITER_IDLE.equals(event.state())) { 
                Request request = new Request();
                request.setClassName("aaaa");
                request.setMethodName("bbb");
                ctx.channel().writeAndFlush(request);
                fcount++;
            }
        }
    }
    
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Response msg) throws Exception {
		System.out.println("收到响应" + msg.getResult().toString());
	}

}










