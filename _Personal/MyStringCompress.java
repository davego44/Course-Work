public class MyStringCompress {
	public static void main(String[] args) {
		System.out.println(compress("aabccccaaa"));
		System.out.println(compress("aaaaa"));
		System.out.println(compress("abcdefghijk"));
		System.out.println(compress(""));
		System.out.println(compress("aBBbbb"));
	}
	
	public static String compress(String s) {
		if (s.length() == 0) return s;
		int currentCount = 0;
		char currentChar = s.charAt(0);
		StringBuilder sb = new StringBuilder();
		for (char c : s.toCharArray()) {
			if (c != currentChar) {
				sb.append(currentChar + "" + currentCount);
				currentCount = 1;
				currentChar = c;
			} else
				currentCount++;
		}
		sb.append(currentChar + "" + currentCount);
		if (sb.length() > s.length()) return s;
		return sb.toString();
	}
}