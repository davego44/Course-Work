public class MyOneAway {
	public static void main(String[] args) {
		System.out.println(isOneAway("apple", "aple")); //true
		System.out.println(isOneAway("apple", "applz")); //true
		System.out.println(isOneAway("apple", "applzz")); //false
		System.out.println(isOneAway("apple", "apples")); //true
		System.out.println(isOneAway("apple", "applee")); //true
		System.out.println(isOneAway("aaple", "apples")); //false
		System.out.println(isOneAway("apple", "appleee")); //false
	}
	
	public static boolean isOneAway(String s1, String s2) {
		int diff = Math.abs(s1.length() - s2.length());
		String longer = s1.length() > s2.length() ? s1 : s2;
		String shorter = s1.length() > s2.length() ? s2 : s1;
		boolean replace, foundChange = false;
		int shortI = 0;
		if (diff == 0) replace = true;
		else if (diff == 1) replace = false;
		else return false;
		for (int i = 0; i < longer.length(); i++) {
			if (longer.charAt(i) != shorter.charAt(shortI)) {
				if (foundChange) return false;
				else foundChange = true;
				if (!replace) shortI--;
			}
			shortI++;
			if (shortI >= shorter.length()) shortI--;
		}
		return true;
	}
}