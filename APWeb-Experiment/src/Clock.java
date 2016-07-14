
public class Clock {
	private static long time = 0;
	private static long start = 0;

	public static void start() {
		start = System.currentTimeMillis();
	}

	public static void end() {
		time += System.currentTimeMillis() - start;
	}

	public static void showTime() {
		System.out.println("Clock ticks " + time + "ms.");
	}
}
