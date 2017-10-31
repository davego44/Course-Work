class MyPower {
	public static void main(String[] args){
		for (int i = 1; i < 5; i++) {
			for (int j = -5; j < 5; j++) {
				System.out.println(i + "^" + j + " = " + power(i, j));
			}
		}
	}
	
	public static double power(double n, int exp) {
		if (exp == 0)
			return 1;
		boolean negative = false;
		if (exp < 0)
			negative = true;
		exp = Math.abs(exp);
		double a;
		if (exp % 2 == 0) {
			a = power(n, exp / 2);
			a *= a;
		} else {
			a = power(n, (exp - 1) / 2);
			a *= a * n;
		}
		if (negative) {
			return 1.0 / a;
		} else {
			return a;
		}	
	}
}