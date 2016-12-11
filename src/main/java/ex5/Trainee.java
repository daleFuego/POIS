package ex5;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Trainee {

	private static int dimension, degree, rows, lines;
	private static List<Double> input, desc;
	private static List<List<Double>> descValues, inputValues;

	public static void main(String[] args) throws Exception {

		/*
		 * if (true) { args = new String[2]; args[0] = "-d"; args[1] =
		 * Trainee.class.getClassLoader().getResource("ex2_description.txt").
		 * getFile();
		 * 
		 * System.setIn(new
		 * FileInputStream(Trainee.class.getClassLoader().getResource(
		 * "ex2_in.txt").getFile())); }
		 */

		runTrainee(args);
	}

	@SuppressWarnings("resource")
	private static void runTrainee(String[] args) throws Exception {
		input = new ArrayList<Double>();
		desc = new ArrayList<Double>();
		int index = 2;
		int iterator = 0;

		Scanner scanner = new Scanner(new File(args[1]));
		while (scanner.hasNext()) {
			desc.add(Double.valueOf(scanner.next()));
		}

		dimension = desc.get(0).intValue();
		degree = desc.get(1).intValue();
		rows = (desc.size() - 2) / (degree + 1);

		descValues = new ArrayList<List<Double>>(rows);

		for (int i = 0; i < rows; i++) {
			descValues.add(new ArrayList<>(degree + 1));
			for (int j = 0; j < degree + 1; j++) {
				descValues.get(i).add(desc.get(index));
				index++;
			}
		}

		scanner = new Scanner(System.in);
		while (scanner.hasNext()) {
			input.add(Double.valueOf(scanner.next()));
		}

		lines = input.size() / (dimension);

		inputValues = new ArrayList<List<Double>>(lines);
		for (int i = 0; i < lines; i++) {
			inputValues.add(new ArrayList<>(dimension));
			for (int j = 0; j < dimension; j++) {
				inputValues.get(i).add(input.get(iterator));
				iterator++;
			}
		}

		for (int i = 0; i < lines; i++) {
			calculateResults(inputValues.get(i), i);
		}
	}

	private static void calculateResults(List<Double> readLine, int index) {
		double result = 0;
		double tmp = 1;
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < degree; j++) {
				tmp *= value(descValues.get(i).get(j), index);
			}
			tmp *= descValues.get(i).get(degree);
			result += tmp;
			tmp = 1;
		}
		
		System.out.println(result);
	}

	private static double value(Double y, int i) {
		if (y == 0) {
			return 1;
		}
		y -= 1;
		return inputValues.get(i).get(y.intValue());
	}
}
