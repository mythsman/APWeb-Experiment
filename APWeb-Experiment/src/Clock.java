import java.util.HashMap;
import java.util.Map;

public class Clock {
	private long time = 0;
	private long start = 0;
	private String name;
	private static Map<String, Clock> map = new HashMap<String, Clock>();

	private Clock(String name) {
		this.name = name;
	}

	public static Clock getClock(String name) {

		if (map.containsKey(name)) {
			return map.get(name);
		} else {
			Clock newClock = new Clock(name);
			map.put(name, newClock);
			return newClock;
		}
	}

	public void close() {
		map.remove(name);
	}

	public void reset() {
		time = 0;
	}

	public void start() {
		start = System.currentTimeMillis();
	}

	public void end() {
		time += System.currentTimeMillis() - start;
	}

	public void show(String s) {
		System.out.println(s + time + "ms");
	}

	public void show(String s, String s2) {
		System.out.println(s + time + "ms" + s2);
	}
}
