package ex1;

import java.util.ArrayList;
import java.util.Scanner;

public class Sorting {

	public static void main(String[] args) {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		ArrayList<Integer> inputValues = new ArrayList<>();
		while (scanner.hasNextInt()) {
			inputValues.add(scanner.nextInt());
		}
		if (inputValues == null || inputValues.isEmpty()) {
			System.err.println("Invalid arguments - exiting...");
			System.exit(0);
		}

		printArray(sortArray(inputValues));
	}

	private static ArrayList<Integer> sortArray(ArrayList<Integer> array) {

		quicksort(array, 0, array.size() - 1);

		return array;
	}

	private static void quicksort(ArrayList<Integer> sortedValues, int x, int y) {

		int i, j, v, temp;
		i = x;
		j = y;
		v = sortedValues.get(i);
		do {
			while (sortedValues.get(i) < v) {
				i++;
			}
			while (v < sortedValues.get(j)) {
				j--;
			}
			if (i <= j) {
				temp = sortedValues.get(i);
				sortedValues.set(i, sortedValues.get(j));
				sortedValues.set(j, temp);
				i++;
				j--;
			}
		} while (i <= j);
		if (x < j)
			quicksort(sortedValues, x, j);
		if (i < y)
			quicksort(sortedValues, i, y);
	}

	private static void printArray(ArrayList<Integer> array) {

		if (array == null || array.isEmpty()) {
			System.err.println("Nothing to show - exiting...");
			System.exit(0);
		}

		for (int index = 0; index < array.size(); index++) {
			System.out.println(array.get(index));
		}
	}

}
