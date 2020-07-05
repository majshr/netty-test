package v1.test01.callback;
/**
 * 回调方法接口
 * @author maj
 *
 */
public interface FetcherCallBack {
	/**
	 * Fetcher方法成功回调
	 * @param data
	 */
	void onData(Data data);
	
	/**
	 * Fetcher方法错误回调
	 * @param cause
	 */
	void onError(Throwable cause);
}
