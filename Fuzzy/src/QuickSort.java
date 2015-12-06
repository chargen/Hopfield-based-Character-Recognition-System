

public class QuickSort<T> {
	private int[] rankArray;
	private int[] unsortedArray;
	
	//for Integer array sorting
	public void setIntArray(int[] array){
		setSortArray(null, array);
	}
	
	/**
	 * Allows sorting of multiple array types
	 * @param array: abstract data type array to be defined at class call
	 * @param rank: rank of abstract data values to assist in abstract data sorting
	 */
	public void setSortArray(int[] matchList, int[] rank){
		rankArray = rank;
		unsortedArray = matchList;
		
	}
	
	/**
	 * Determines median value as pivot
	 * @param lo: initial position of array /sub-array value for locating average
	 * @param hi: highest position of array /sub-array value for locating average
	 * @return pivot location
	 */
	private int findPivot(int lo, int hi){
		final int mid = (lo+hi)/2;
		int median_value = (rankArray[lo] + rankArray[hi] + rankArray[mid])/3;
		int close = lo;
		for(int i = 0; i < hi; i++){
			if(rankArray[i] == median_value)
				return i;
			else if(Math.abs(median_value - rankArray[i]) < Math.abs(median_value - rankArray[close]))
				close = i;	
		}
		return close;
	}

	/**
	 * Runs Quick-Sort Algorithm on provided array
	 * @param lo: initial position to be sorted
	 * @param hi: last position to be sorted
	 * @param print: boolean value; print array after each sort if true
	 */
	public int[] sortArray(int lo, int hi, boolean print){
		
		int lower = lo, high = (hi - 1);
		int pivot = rankArray[findPivot(lower, high)];
		while(lower <= high){
			while(rankArray[lower] < pivot)
				lower++;
			while(rankArray[high] > pivot)
				high--;
			if(lower <= high){
				swapVal(lower, high);
				lower++;
				high--;
				if(print)
					printArray();
			}
		}
		if(lo < high)
			sortArray(lo, high, print);
		if(hi > lower)
			sortArray(lower, hi, print);
		
		
		return unsortedArray;
	}
	
	//Default sortArray without print feature
	public int[] sortArray(int lo, int hi){
		return sortArray(lo, hi, false);
	}
	
	//Prints current array values
	public void printArray(){
		if(unsortedArray == null){
			for(int val : rankArray)
				System.out.printf(" %d",val);
		}else{
			for(int val : unsortedArray)
				System.out.print(" " + val);
		}
		System.out.println();
	}
	
	public int[] getRankArray(){
		return rankArray;
	}
	
	/**
	 * Swaps given array values depending on null value of unsortedArray
	 * @param a: position to be swapped with b
	 * @param b: position to be swapped with a
	 */
	private void swapVal(int a, int b){
		int temp = rankArray[a];
		rankArray[a] = rankArray[b];
		rankArray[b] = temp;
		if(unsortedArray != null){
			int tempVal = unsortedArray[a];
			unsortedArray[a] = unsortedArray[b];
			unsortedArray[b] = tempVal;
		}
	}
	
}
