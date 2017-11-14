public class MyURLify {
	public static void main(String[] args) {
		System.out.println(urlify("My John Smith    ", 13));
		System.out.println(urlify("   ", 1));
		System.out.println(urlify("Oh", 2));
		System.out.println(urlify("Mdsa sas sws s  sas          ", 19));
	}
	
	public static String urlify(String s, int trueSize) {
		int pos = trueSize - 1;
		char[] answer = s.toCharArray();
		for (int i = s.length() - 1; i >= 0; i--) {
			if (pos == i) break;
			if (answer[pos] == ' ') {
				answer[i--] = '0';
				answer[i--] = '2';
				answer[i] = '%';
			} else {
				answer[i] = answer[pos];
			}
			pos--;
		}
		return new String(answer);
	}
}