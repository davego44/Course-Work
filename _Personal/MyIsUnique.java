import java.util.HashSet;

public class MyIsUnique {
	public static void main(String[] args) {
		System.out.println(isUnique(args[0]));
	}
	
	public static boolean isUnique(String s) {
		HashSet<Character> hs = new HashSet<Character>();
		for (char c : s.toCharArray()) {
			if (hs.contains(c))
				return false;
			hs.add(c);
		}
		return true;
	}
}