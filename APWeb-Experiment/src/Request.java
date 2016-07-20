import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

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

		URLConnection connection = realUrl.openConnection();
		connection.setRequestProperty("accept", "*/*");
		connection.setRequestProperty("connection", "Keep-Alive");
		connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
		connection.connect();
		try {
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			long t2 = System.currentTimeMillis();
			System.out.println("Response in " + (t2 - t1) + " ms.");
			JSONObject json = new JSONObject(result);
			JSONArray arr = json.getJSONArray("resourceSets");
			json = arr.getJSONObject(0);
			arr = json.getJSONArray("resources");
			json = arr.getJSONObject(0);
			arr = json.getJSONArray("routeLegs");
			ArrayList<Location> res = new ArrayList<Location>();
			res.add(q.getUser());
			for (int i = 0; i < arr.length() - 1; i++) {
				json = arr.getJSONObject(i);
				JSONArray end = json.getJSONObject("actualEnd").getJSONArray("coordinates");
				JSONArray start = json.getJSONObject("actualStart").getJSONArray("coordinates");
				double latitude = Double
						.parseDouble(end.toString().substring(1, end.toString().length() - 1).split(",")[0]);
				double longitude = Double
						.parseDouble(end.toString().substring(1, end.toString().length() - 1).split(",")[1]);
				res.add(new Location(longitude, latitude));
			}
			res.add(q.getPoi());

			ArrayList<Location> raw = new ArrayList<Location>();
			raw.add(q.getUser());
			raw.addAll(q.getWaypoints());
			raw.add(q.getPoi());
			int count = 0;
			for (int i = 0; i < raw.size(); i++) {
				for (int j = 0; j < res.size() - 1; j++) {
					double d = res.get(j).distanceToLoc(res.get(j + 1));
					double d1 = raw.get(i).distanceToLoc(res.get(j));
					double d2 = raw.get(i).distanceToLoc(res.get(j + 1));
					if (d1 + d2 - d < 20) {
						count++;
						break;
					}
				}
			}
			System.out.println("Target " + count + " in " + raw.size());
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