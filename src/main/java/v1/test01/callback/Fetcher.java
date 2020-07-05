package v1.test01.callback;

/**
 * 接口，进行具体业务逻辑, 有一个方法
 * 方法参数为回调实现类，如果接口成功或失败，会调用传入参数的回调
 * @author bhz（maj）
 * @since 2019年6月2日
 */
public interface Fetcher {
	void fetchData(FetcherCallBack callback);
}
