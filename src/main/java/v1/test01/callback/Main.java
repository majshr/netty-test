package v1.test01.callback;

public class Main {
	public static void main(String[] args) {
		// fetcher对象, 用于具体业务处理
		Fetcher fetcher = new MyFetcher(new Data(1, 2));
		
		// 调用处理方法, 指定处理回调
		fetcher.fetchData(new FetcherCallBack() {
			
			public void onError(Throwable cause) {
				System.out.println("执行错误" + cause.getMessage());
			}
			
			public void onData(Data data) {
				System.out.println("执行成功" + data);
			}
		});
	}
}
