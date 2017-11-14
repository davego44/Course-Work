public class MyChangeMaking {
	public static void main(String[] args) {
		int [] results = getChange(289);
		for (int i : results) {
			System.out.println(i);
		}
	}
	
	public static int[] getChange(int numberOfCents) {
		int [] coins = {100, 25, 10, 5, 1};
		int [] numberOfCoins = {0, 0, 0, 0, 0};
		int index = 0;
		while (numberOfCents > 0) {
			numberOfCoins[index] = numberOfCents / coins[index];
			numberOfCents %= coins[index];
			index++;
		}
		return numberOfCoins;
	}
}