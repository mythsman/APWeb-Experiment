import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

//http://dev.virtualearth.net/REST/V1/Routes?wp.0=37.779160067439079,-122.42004945874214&wp.1=32.715685218572617,-117.16172486543655&wp.2=32.715685218572617,-117.16172486543655&key=AsZOWyYLr14AKviF4sCb1pjf6Mxp4U79toQ9hpFlAawE9V6tGnjPCq5JV4hBHIOG
public class Request extends Thread {
	private LocationList list;
	private long time;
	private ArrayList<Location> res;

	public long getTime() {
		return time;
	}

	public Request(LocationList q) {
		this.list = q;
		res = new ArrayList<Location>();
	}

	public void request() throws IOException {

		String result = "";
		BufferedReader in = null;

		String url = "http://dev.virtualearth.net/REST/V1/Routes?";
		for (int i = 0; i < list.size(); i++) {
			url += "wp." + i + "=" + list.get(i).toString() + "&";
		}
		url += "key=AiF5rjhPwqK8UOyzmg8yxFqILQcRouLZiz8JeoEORTRGzpFjAnriYJoRg49KgctN";
		
		//url += "key=AsZOWyYLr14AKviF4sCb1pjf6Mxp4U79toQ9hpFlAawE9V6tGnjPCq5JV4hBHIOG";
		URL realUrl = new URL(url);

		URLConnection connection = realUrl.openConnection();
		connection.setRequestProperty("accept", "*/*");
		connection.setRequestProperty("connection", "Keep-Alive");
		connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
		connection.connect();
		try {
			Clock.getClock(list.toString()).start();
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
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
			res.clear();
			for (int i = 0; i < arr.length() - 1; i++) {
				json = arr.getJSONObject(i);
				JSONArray end = json.getJSONObject("actualEnd").getJSONArray("coordinates");
				double latitude = Double
						.parseDouble(end.toString().substring(1, end.toString().length() - 1).split(",")[0]);
				double longitude = Double
						.parseDouble(end.toString().substring(1, end.toString().length() - 1).split(",")[1]);
				res.add(new Location(longitude, latitude));
			}
			Clock.getClock(list.toString()).end();
			Clock.getClock(list.toString()).show("Request in ");
		} catch (Exception e) {
			e.printStackTrace();
			request();
		}
	}

	@Override
	public void run() {
		try {
			request();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Location> getRes() {
		return res;
	}

	public static void main(String[] args) throws IOException {

	}
}