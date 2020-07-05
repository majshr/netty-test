package netty92.study07.protobuf.netty;

import java.util.Random;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import netty92.study07.protobuf.entity.MyDataInfo;
import netty92.study07.protobuf.entity.MyDataInfo.Cat;
import netty92.study07.protobuf.entity.MyDataInfo.Dog;
import netty92.study07.protobuf.entity.MyDataInfo.MyMessage;
import netty92.study07.protobuf.entity.MyDataInfo.Person;
import netty92.study07.protobuf.entity.MyDataInfo.MyMessage.DataType;

public class MyClientHandler extends SimpleChannelInboundHandler<MyDataInfo.MyMessage>{

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, MyMessage msg) throws Exception {
		
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// 通道激活后，向服务端发送消息
		Random random = new Random();
		for(int i = 0; i < 10; i++){
			int num = random.nextInt(3);
			num++;
			MyMessage message = null;
			switch(num){
				case 1:
					message = MyDataInfo.MyMessage.newBuilder().setDataType(DataType.PersonType)
						.setPerson(Person.newBuilder().setName("一个人").setAge(27).setAddress("孟角").build())
						.build();
					break;
				case 2:
					message = MyDataInfo.MyMessage.newBuilder().setDataType(DataType.DogType)
					.setDog(Dog.newBuilder().setName("狗").setAge(10).build())
					.build();
					break;
				case 3:
					message = MyDataInfo.MyMessage.newBuilder().setDataType(DataType.CatType)
					.setCat(Cat.newBuilder().setName("猫").setAge(5).build())
					.build();
					break;
			}
			ctx.writeAndFlush(message);
		}
	}
}
