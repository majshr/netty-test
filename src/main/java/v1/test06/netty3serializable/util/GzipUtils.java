package v1.test06.netty3serializable.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 对二进制数据进行压缩和解压缩工具类
 * @author maj
 */
public class GzipUtils {
	/**
	 * 压缩字节数组
	 * @param data
	 * @return byte[]
	 * @throws IOException
	 */
	public static byte[] gzip(byte[] data) throws IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(bos);
		gzip.write(data);
		gzip.finish();
		gzip.close();
		
		byte[] ret = bos.toByteArray();
		bos.close();
		return ret;
	}
	
	/**
	 * 解压缩数据进行解压缩
	 * @param data 压缩后的数据
	 * @return byte[] 原本的数据
	 * @throws IOException 
	 */
	public static byte[] ungzip(byte[] data) throws IOException{
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		GZIPInputStream gzip = new GZIPInputStream(bis);
		
		byte[] buf = new byte[1024];
		int num = -1;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while((num = gzip.read(buf, 0, buf.length)) != -1){
			// 将解压缩后的字节数组拼接起来
			bos.write(buf, 0, num);
		}
		
		gzip.close();
		bis.close();
		
		byte[] ret = bos.toByteArray();
		bos.flush();
		bos.close();
		return ret;
	}
	
    public static void main(String[] args) throws Exception{
		
    	//读取文件
    	String readPath = "C:\\Users\\maj\\Desktop\\spring.java";
        File file = new File(readPath);  
        FileInputStream in = new FileInputStream(file);  
        byte[] data = new byte[in.available()];  
        in.read(data);  
        in.close();  
        
        System.out.println("文件原始大小:" + data.length);
        //测试压缩
        
        byte[] ret1 = GzipUtils.gzip(data);
        System.out.println("压缩之后大小:" + ret1.length);
        
        byte[] ret2 = GzipUtils.ungzip(ret1);
        System.out.println("还原之后大小:" + ret2.length);
        
        //写出文件
        String writePath = "C:\\Users\\maj\\Desktop\\spring_copy.java";
        FileOutputStream fos = new FileOutputStream(writePath);
        fos.write(ret2);
        fos.close();    	
    	
    	
	}
}









































