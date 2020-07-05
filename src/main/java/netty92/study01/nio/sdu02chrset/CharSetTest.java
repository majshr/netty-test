package netty92.study01.nio.sdu02chrset;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

public class CharSetTest {
    public static void main(String[] args) {
        Charset charset = Charset.forName("UTF-8");
        // 编码
        ByteBuffer buffer = charset.encode("aaa");
        System.out.println(buffer);
        System.out.println(Arrays.toString(buffer.array()));
    }

}
