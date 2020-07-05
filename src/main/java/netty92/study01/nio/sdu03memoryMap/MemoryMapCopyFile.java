package netty92.study01.nio.sdu03memoryMap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
/**
 * 使用内存映射拷贝文件内容
 * @author bhz（maj）
 * @since 2020年7月3日
 */
public class MemoryMapCopyFile {
    public static int fileLen = 0;
    
    /**
     * 使用内存映射读取文件
     * @param filePath
     * @return
     */
    private static ByteBuffer readFile(String filePath) {
        try {
            RandomAccessFile randAccessFile = new RandomAccessFile(filePath, "rw");
            fileLen = (int) randAccessFile.length();
            FileChannel channel = randAccessFile.getChannel();
            /*
             * FileChannel.MapMode.READ_ONLY
             * FileChannel.MapMode.READ_WRITE：读写缓冲区,
             * 任何时刻如果通过内存映射的方式修改了文件则立刻会对磁盘上的文件执行相应的修改操作。别的进程如果也共享了同一个映射，
             * 则也会同步看到变化。而不是像标准IO那样每个进程有各自的内核缓冲区，比如JAVA代码中，没有执行 IO输出流的 flush()
             * 或者 close() 操作，那么对文件的修改不会更新到磁盘去，除非进程运行结束；
             * FileChannel.MapMode.PRIVATE
             * ：这个比较狠，可写缓冲区，但任何修改是缓冲区私有的，不会回到文件中。所以尽情的修改吧，结局跟突然停电是一样的。
             */
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, randAccessFile.length());
            return buffer;
//            return buffer.get(new byte[(int) randAccessFile.length()]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 复制文件
     * @param fromPath
     * @param toPath
     */
    public static void copyFile(String fromPath, String toPath) {
        try {
            RandomAccessFile toFile = new RandomAccessFile(toPath, "rw");

            ByteBuffer fromBuf = readFile(fromPath);

            FileChannel toChannel = toFile.getChannel();
            MappedByteBuffer toBuf = toChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileLen);

            int i = 0;
            int len = fromBuf.position();
            // one byte one byte copy
            // while (i < len) {
            // toBuf.put(fromBuf.get(i));
            // i++;
            // }

            toBuf.flip();
            // all byte copy
            toBuf.put(fromBuf);
            // toBuf.put(fromBuf);


            toBuf.flip();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        copyFile("C:\\Users\\Administrator\\Desktop\\image_copy\\maj_test.tar",
                "C:\\Users\\Administrator\\Desktop\\image_copy\\maj_test_copy.tar");
        System.out.println("=====================");
    }
}
