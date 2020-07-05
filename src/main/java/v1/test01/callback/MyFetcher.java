package v1.test01.callback;

/**
 * Fetcher实现
 * @author maj
 *
 */
public class MyFetcher implements Fetcher {
	final Data data;
	
	public MyFetcher(Data data) {
		this.data = data;
	}
	
	/**
	 * 方法执行成功或失败，会进行回调
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：</B><BR>
	 * @see v1.test01.callback.Fetcher#fetchData(v1.test01.callback.FetcherCallBack)
	 */
	public void fetchData(FetcherCallBack callback) {
		try{
			// ...
			
			// 执行成功回调 
			callback.onData(data);
		}catch(Exception e){
			// 执行错误回调
			callback.onError(e);
		}
	}

}
