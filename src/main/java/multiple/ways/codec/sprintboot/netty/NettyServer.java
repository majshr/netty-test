package multiple.ways.codec.sprintboot.netty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "netty")
public class NettyServer {
	private String host;
	private Integer port;
	private Integer boossGroupThreadCount;
	private Integer workerGroupThreadCount;
	
}


















