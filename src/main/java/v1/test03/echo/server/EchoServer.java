package v1.test03.echo.server;

import java.util.Iterator;
import java.util.Random;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;
/**
 * 服务端监听连接, 有连接建立后, 向客户端发送数据
 * @author bhz（maj）
 * @since 2020年7月1日
 */
public class EchoServer {
	private final int port;
	
	private ByteBuf buf = Unpooled.copiedBuffer("your data", CharsetUtil.UTF_8);
	
	public EchoServer(int port){
		this.port = port;
	}
	
	public void start() throws Exception{
		// 接收和处理新连接
		EventLoopGroup group = new NioEventLoopGroup();
		try{
			ServerBootstrap b = new ServerBootstrap();
			b.group(group).channel(NioServerSocketChannel.class)
			.localAddress(port)
			.childHandler(new ChannelInitializer<Channel>() {// 指定连接后调用的ChannelHandler

				@Override
				protected void initChannel(Channel ch) throws Exception {
//					ch.pipeline().addLast(new EchoServerHandler());
					
					// 通过通道向连接写数据, 监听写数据完成 
					ChannelFuture f = ch.writeAndFlush(buf);
					f.addListener(new ChannelFutureListener() {
						
						public void operationComplete(ChannelFuture future) throws Exception {
							if(future.isSuccess()){
								System.out.println("发送数据成功");
							}
						}
					});
					
				}
			});
			
			// 阻塞知道服务器完成绑定
			ChannelFuture f = b.bind().sync();
			System.out.println("服务启动.........");
			
			// 关闭操作, 也会阻塞
			f.channel().closeFuture().sync();
		}finally{
			group.shutdownGracefully().sync();
		}
	}
	
	public static void main(String[] args) throws Exception {
		new EchoServer(8080).start();
	}
	
	private void compositeByteBufTest(){
		CompositeByteBuf compBuf = Unpooled.compositeBuffer();
		// 堆缓冲区
		ByteBuf heapBuf = Unpooled.buffer(8);
		// 直接缓冲区
		ByteBuf directBuf = Unpooled.directBuffer(16);
		compBuf.addComponents(heapBuf, directBuf);
		
		// 删除第一个缓冲区
		compBuf.removeComponent(0);
		
		// 遍历复合缓冲区
		Iterator<ByteBuf> iter = compBuf.iterator();
		while(iter.hasNext()){
			System.out.println(iter.next().toString());
		}
		
		// 使用数组访问
		if(!compBuf.hasArray()){
			int len = compBuf.readableBytes();
			byte[] arr = new byte[len];
			compBuf.getBytes(0, arr);
		}
	}
	
	public void bufTest(){
		// 通过索引访问, 不会推进读索引和写索引, 可以通过readerIndex()和writerIndex()推进
		ByteBuf buf = Unpooled.buffer(16);
		for(int i = 0; i < 16; i++){
			buf.writeByte(i + 1);
		}
		
		for(int i = 0; i < buf.capacity(); i++){
			System.out.println(buf.getByte(i));
		}
		
		/* 废弃字节, 丢弃从索引0到readIndex之间的字节, 可以用来清空ByteBuf中已经读取的数据, 从而使
		   ByteBuf有多余的空间容纳新数据, 但会涉及到内存复制, 移动可读字节到开始位置, 可能会影响性能.*/
		buf.discardReadBytes();
		
		/*循环读取, 直到读完*/
		while(buf.isReadable()){
			System.out.println(buf.readByte());
		}
		
		/*循环写入, 直到写完*/
		Random random = new Random();
		while(buf.isWritable()){
			buf.writeInt(random.nextInt());
		}
		
		/*缓冲区恢复为初始, read/write index均为0*/
		buf.clear();
	}
}
























