import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

//http://dev.virtualearth.net/REST/V1/Routes?wp.0=37.779160067439079,-122.42004945874214&wp.1=32.715685218572617,-117.16172486543655&wp.2=32.715685218572617,-117.16172486543655&key=AsZOWyYLr14AKviF4sCb1pjf6Mxp4U79toQ9hpFlAawE9V6tGnjPCq5JV4hBHIOG
public class Request extends Thread {
	private LocationList list;
	private long time;
	private LocationList res;

	public long getTime() {
		return time;
	}

	public Request(LocationList q) {
		this.list = q;
		res = new LocationList();
	}

	public static String[] urls = {
			"AsZOWyYLr14AKviF4sCb1pjf6Mxp4U79toQ9hpFlAawE9V6tGnjPCq5JV4hBHIOG",
			"AiF5rjhPwqK8UOyzmg8yxFqILQcRouLZiz8JeoEORTRGzpFjAnriYJoRg49KgctN",
			"AvBMPrSwSYiANzMNuBvzk3zEfv1jGbGSmTr9wU7Tv_qiXTVDdH5nKDnaUJQlJ3vP",
			"ApPND2d30kmDtW6AFDYOCNcIBu753PHSYuMtTmfBvIfa7Airtlejo8YLZqYBKWD1",
			"Apenr-qQjqHmaKqELsCTx3MD9KrqqIJvkDv-xcRVJcDK09SYJwWmIA40z2jNiqik",
			"AutcsYM3LnXhou46MImyJFSieb_gHzk_OFfNkd3VJb7EH5y5hMdCRwCt_Qalm5ef", };

	public static int keyNum = 0;

	public synchronized String getUrl() {
		String res = urls[keyNum];
		keyNum++;
		if (keyNum >= urls.length) {
			keyNum = 0;
		}
		return res;
	}

	public void request() throws IOException {
		Clock clock = Clock.getClock(list.toString());
		clock.start();
		String result = "";
		BufferedReader in = null;

		String url = "http://dev.virtualearth.net/REST/V1/Routes?";

		for (int i = 0; i < list.size(); i++) {
			url += "wp." + i + "=" + list.get(i).toString() + "&";
		}
		url += "key=" + getUrl();
		URL realUrl = new URL(url);

		URLConnection connection = realUrl.openConnection();
		connection.setRequestProperty("accept", "*/*");
		connection.setRequestProperty("connection", "Keep-Alive");
		connection.setRequestProperty("user-agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

		try {
			connection.connect();
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			JSONObject json = new JSONObject(result);
			JSONArray arr = json.getJSONArray("resourceSets");
			json = arr.getJSONObject(0);
			arr = json.getJSONArray("resources");
			json = arr.getJSONObject(0);
			arr = json.getJSONArray("routeLegs");
			for (int i = 0; i < arr.length() - 1; i++) {
				json = arr.getJSONObject(i);
				JSONArray end = json.getJSONObject("actualEnd").getJSONArray(
						"coordinates");
				double latitude = Double
						.parseDouble(end.toString()
								.substring(1, end.toString().length() - 1)
								.split(",")[0]);
				double longitude = Double
						.parseDouble(end.toString()
								.substring(1, end.toString().length() - 1)
								.split(",")[1]);
				res.add(new Vertex(longitude, latitude, -1));
			}

		} catch (Exception e) {
			// e.printStackTrace();
		}
		clock.end();
		res.setResponseTime(clock.getTime());
		list.setResponseTime(clock.getTime());
		// clock.show("Request in ");
	}

	@Override
	public void run() {
		try {
			request();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public LocationList getRes() {
		return res;
	}

	public static void main(String[] args) throws IOException {

	}
}