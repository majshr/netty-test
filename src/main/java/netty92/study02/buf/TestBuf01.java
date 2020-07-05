package netty92.study02.buf;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

public class TestBuf01 {
	
	// 聚合buf
	public static void main3(String[] args) {
		CompositeByteBuf compositeByteBuf = Unpooled.compositeBuffer();
		ByteBuf heapBuf = Unpooled.buffer(10);
		ByteBuf directBuf = Unpooled.directBuffer(8);
		
		compositeByteBuf.addComponents(heapBuf, directBuf);
		
//		compositeByteBuf.removeComponent(0);
		
		Iterator<ByteBuf> iter = compositeByteBuf.iterator();
		while(iter.hasNext()) {
			System.out.println(iter.next());
		}
	}
	
	public static void main2(String[] args) {
		// 一定要指定字符集，不同字符集，字节数不一样
		ByteBuf buf = Unpooled.copiedBuffer("章hello world!", Charset.forName("utf-8"));
		
		// 真正存储数据是用一个字节数组存储的，为true
		// 如果buf是字节数组，就是堆上缓冲；如果不是，就是堆外内存（操作系统分配）
		if(buf.hasArray()) {
			byte[] content = buf.array();
			System.out.print(new String(content, Charset.forName("utf-8")));
			
			// 数组偏移量
			System.out.println(buf.arrayOffset());
			System.out.println(buf.readerIndex());
			System.out.println(buf.writerIndex());
			System.out.println(buf.capacity());
			
			System.out.println(buf.readableBytes()); 
			
			for(int i = 0; i < buf.readableBytes(); ++i) {
				// 根据下表获取，不改变readerIndex值
				// 中文的三个字节会乱码
				System.out.print((char)buf.getByte(i));
			}
			
			System.out.println();
			
			// 中文的三个字节会可以正常打印
			System.out.println(buf.getCharSequence(0, 4, Charset.forName("utf-8")));
			System.out.println(buf.getCharSequence(4, 3, Charset.forName("utf-8")));
		}
	}
	
	// 
	public static void main1(String[] args) {
		// 非池的buffer
		ByteBuf buffer = Unpooled.buffer(10);
		for(int i = 0; i < 10; i++) {
			buffer.writeByte(i);
		}
		for(int i = 0; i < buffer.capacity(); i++) {
			System.out.println(buffer.getByte(i));
		}
	}
}
