import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

//http://dev.virtualearth.net/REST/V1/Routes?wp.0=37.779160067439079,-122.42004945874214&wp.1=32.715685218572617,-117.16172486543655&wp.2=32.715685218572617,-117.16172486543655&key=AsZOWyYLr14AKviF4sCb1pjf6Mxp4U79toQ9hpFlAawE9V6tGnjPCq5JV4hBHIOG
public class Request {
	public static long request(Query q) throws IOException {

		long t1 = System.currentTimeMillis();
		String result = "";
		BufferedReader in = null;

		String url = "http://dev.virtualearth.net/REST/V1/Routes?";
		url += "wp.0=" + q.getUser().toString();
		int cnt = 0;
		for (Location loc : q.getWaypoints()) {
			url += "&wp." + ++cnt + "=" + loc.toString();
		}
		url += "&wp." + ++cnt + "=" + q.getPoi().toString();
		url += "&key=AsZOWyYLr14AKviF4sCb1pjf6Mxp4U79toQ9hpFlAawE9V6tGnjPCq5JV4hBHIOG";
		URL realUrl = new URL(url);
		// 打开和URL之间的连接
		URLConnection connection = realUrl.openConnection();
		// 设置通用的请求属性
		connection.setRequestProperty("accept", "*/*");
		connection.setRequestProperty("connection", "Keep-Alive");
		connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
		// 建立实际的连接
		connection.connect();
		// 获取所有响应头字段
		try {
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			long t2 = System.currentTimeMillis();
			System.out.println("Response in " + (t2 - t1) + " ms.");

			System.out.println(result);
			return t2 - t1;
		} catch (Exception e) {
			return request(q);
		}
	}

	public static void main(String[] args) throws IOException {
		Query q = new Query(new Location(-122.42004945874214, 37.779160067439079),
				new Poi(-117.16172486543655, 32.715685218572617, 2));
		request(q);
	}
}