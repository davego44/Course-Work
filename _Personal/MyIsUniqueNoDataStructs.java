public class MyIsUniqueNoDataStructs {
	public static void main(String[] args) {
		System.out.println(isUnique(args[0]));
	}
	
	public static boolean isUnique(String s) {
		int[] bits = new int[256];
		for (int i = 0; i < s.length(); i++) {
			if (bits[(int)s.charAt(i)] == 1)
				return false;
			bits[(int)s.charAt(i)] = 1;
		}
		return true;
	}
}