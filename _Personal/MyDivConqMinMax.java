class MyDivConqMinMax {
	public static void main(String[] args) {
		int[] array1 = {2,4,1,7,4,2,5,34,23,304,34,5,8,33,49};
		int[] array2 = {5,4,7,3,9,3,1};
		int[] array3 = {3,2,2};
		int[] array1MinMax = findMinMax(array1);
		int[] array2MinMax = findMinMax(array2);
		int[] array3MinMax = findMinMax(array3);
		System.out.println("ARRAY 1 {2,4,1,7,4,2,5,34,23,304,34,5,8,33,49} : Min = " + array1MinMax[0] + " Max = " + array1MinMax[1]);
		System.out.println("ARRAY 2 {5,4,7,3,9,3,1} : Min = " + array2MinMax[0] + " Max = " + array2MinMax[1]);
		System.out.println("ARRAY 3 {3,2,2} : Min = " + array3MinMax[0] + " Max = " + array3MinMax[1]);
	}
	
	public static int[] findMinMax(int[] array) {
		return findMinMax(array, 0, array.length - 1);
	}
	
	public static int[] findMinMax(int[] orig, int beginIndex, int endIndex) {
		int[] minMax = new int[2];
		if (endIndex - beginIndex <= 1) {
			minMax[0] = Math.min(orig[beginIndex], orig[endIndex]);
			minMax[1] = Math.max(orig[beginIndex], orig[endIndex]);
			return minMax;
		}
		int midIndex = (endIndex - beginIndex) / 2;
		int[] tempMinMaxLeft = findMinMax(orig, beginIndex, beginIndex + midIndex);
		int[] tempMinMaxRight = findMinMax(orig, beginIndex + midIndex + 1, endIndex);
		minMax[0] = Math.min(tempMinMaxLeft[0], tempMinMaxRight[0]);
		minMax[1] = Math.max(tempMinMaxLeft[1], tempMinMaxRight[1]);
		return minMax;
	}
}