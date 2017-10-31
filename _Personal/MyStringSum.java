class MyStringSum {
	public static void main(String[] args){
		System.out.println("10 + 20 = " + stringSum("10", "20"));
		System.out.println("1 + 20 = " + stringSum("1", "20"));
		System.out.println("12 + 29 = " + stringSum("12", "29"));
		System.out.println("132 + 20 = " + stringSum("132", "20"));
		System.out.println("1 + 544 = " + stringSum("1", "544"));
		System.out.println("-1 + -1 = " + stringSum("-1", "-1"));
	}
	
	public static String stringSum(String a, String b) {
		boolean addTwoNeg = false;
		if (a.startsWith("-") && b.startsWith("-")) {
			a = a.substring(1);
			b = b.substring(1);
			addTwoNeg = true;
		}
		int aEndIndex = a.length() - 1;
		int bEndIndex = b.length() - 1;
		int sum;
		boolean hasCarry = false;
		StringBuilder sb = new StringBuilder();
		while (aEndIndex > -1 || bEndIndex > -1) {
			sum = (aEndIndex > -1 ? Integer.valueOf(Character.toString(a.charAt(aEndIndex))) : 0) +
				  (bEndIndex > -1 ? Integer.valueOf(Character.toString(b.charAt(bEndIndex))) : 0) +
				  (hasCarry ? 1 : 0);
			sb.insert(0, sum % 10);
			hasCarry = (sum > 9);
			aEndIndex--;
			bEndIndex--;
		}
		if (addTwoNeg)
			sb.insert(0, '-');
		return sb.toString();
	}
}