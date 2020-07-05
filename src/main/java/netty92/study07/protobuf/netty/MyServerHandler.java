package netty92.study07.protobuf.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import netty92.study07.protobuf.entity.MyDataInfo;
import netty92.study07.protobuf.entity.MyDataInfo.MyMessage;

public class MyServerHandler extends SimpleChannelInboundHandler<MyDataInfo.MyMessage>{

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, MyMessage msg) throws Exception {
		MyDataInfo.MyMessage.DataType type = msg.getDataType();
		
		switch(type){
		case CatType:
			MyDataInfo.Cat cat = msg.getCat();
			System.out.println(cat.getName() + "==" + cat.getAge());
			break;
		case DogType:
			MyDataInfo.Dog dog = msg.getDog();
			System.out.println(dog.getName() + "==" + dog.getAge());
			break;
		case PersonType:
			MyDataInfo.Person person = msg.getPerson();
			System.out.println(person.getName() + "==" + person.getAge() + person.getAddress());
			break;
		default:
			break;
			
		}
	}

}
