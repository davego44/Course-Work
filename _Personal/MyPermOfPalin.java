import java.util.HashMap;

public class MyPermOfPalin {
	public static void main(String[] args) {
		System.out.println(isPermOfPalin("Tact Coa")); //true
		System.out.println(isPermOfPalin("baba ")); //true
		System.out.println(isPermOfPalin("dj jd p")); //true
		System.out.println(isPermOfPalin("poppers")); //false
	}
	
	public static boolean isPermOfPalin(String s) {
		int val, oddCount = 0;
		boolean foundOne = false;
		int[] found = new int[128]; //assuming ascii
		for (char c : s.toCharArray()) {
			if (c == ' ') continue;
			c = Character.toLowerCase(c);
			found[(int)c]++;
			if (found[(int)c] % 2 == 0)
				oddCount--;
			else
				oddCount++;
		}
		if (oddCount != 1 && oddCount != 0) return false;
		return true;
	}
}