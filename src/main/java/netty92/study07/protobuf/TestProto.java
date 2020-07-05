package netty92.study07.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;

import netty92.study07.protobuf.entity.MyDataInfo;

public class TestProto {
	public static void main(String[] args) throws InvalidProtocolBufferException {
		// 方法链编程风格
		MyDataInfo.Dog dog = MyDataInfo.Dog.newBuilder().setName("狗").setAge(10).build();
		MyDataInfo.Cat cat = MyDataInfo.Cat.newBuilder().setName("猫").setAge(11).build();
		
		// 转换为二进制信息
		byte[] byteData = dog.toByteArray();
		
		// 解析二进制
		MyDataInfo.Dog newDog = dog.parseFrom(byteData);
		
		System.out.println(newDog.getAge() + "==" + newDog.getName());
		
	}
}
