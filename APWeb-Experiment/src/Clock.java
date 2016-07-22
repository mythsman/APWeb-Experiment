
public class Clock {
	private static long time = 0;
	private static long start = 0;

	public static void start() {
		time = 0;
		start = System.currentTimeMillis();
	}

	public static void end() {
		time += System.currentTimeMillis() - start;
	}

	public static void show(String s) {
		System.out.println(s + time + "ms");
	}

	public static void show(String s, String s2) {
		System.out.println(s + time + "ms" + s2);
	}
}
