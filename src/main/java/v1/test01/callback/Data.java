package v1.test01.callback;

/**
 * 数据，不用关心
 * @author bhz（maj）
 * @since 2019年6月2日
 */
public class Data {
	private int m;
	private int n;
	public Data(int m, int n){
		this.m = m;
		this.n = n;
	}
	@Override
	public String toString() {
		return "Data [m=" + m + ", n=" + n + "]";
	}
}
